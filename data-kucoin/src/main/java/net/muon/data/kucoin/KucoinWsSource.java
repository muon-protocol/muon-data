package net.muon.data.kucoin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import net.muon.data.core.AbstractWsSource;
import net.muon.data.core.PropertyPathValueResolver;
import net.muon.data.core.TokenPair;
import net.muon.data.core.TokenPairPrice;
import org.apache.ignite.Ignite;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KucoinWsSource extends AbstractWsSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KucoinWsSource.class);

    private final ObjectMapper mapper;
    private WebSocketClient client;
    private boolean singleSubscriptionMode = false;

    public KucoinWsSource(String id, List<TokenPair> subscriptionPairs,
                          ExecutorService executor, Ignite ignite, ObjectMapper objectMapper)
    {
        super(id, subscriptionPairs, executor, ignite);
        this.mapper = objectMapper;
    }

    @Override
    public void connect()
    {
        LOGGER.debug("Symbols: {}", subscriptionPairs);
        startWebSocket();
    }

    @Override
    public void disconnect()
    {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOGGER.warn("Failed to disconnect websocket", e);
            }
            client = null;
        }
    }

    private void startWebSocket()
    {
        if (client != null)
            client = new WebSocketClient(getBulletPublic());
        // FIXME: What happens for the last thread when reconnects
        Preconditions.checkState(client == null, "Already started but not closed");
        client = new WebSocketClient(getBulletPublic());
        client.connect();
    }

    private Map<String, Object> getBulletPublic()
    {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.kucoin.com/api/v1/bullet-public"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), new TypeReference<>() {});
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void subscribe()
    {
        if (singleSubscriptionMode) {
            for (TokenPair pair : subscriptionPairs) {
                try {
                    doSubscribe(Collections.singletonList(pair));
                } catch (Exception e) {
                    LOGGER.warn("Exception suppressed", e);
                }
            }
        }
        doSubscribe(subscriptionPairs);
    }

    private void doSubscribe(List<TokenPair> symbols)
    {
        String topic = symbols.stream().map(TokenPair::toString).collect(Collectors.joining(",")).toUpperCase();
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("id", ThreadLocalRandom.current().nextInt());
            msg.put("type", "subscribe");
            msg.put("topic", "/market/ticker:" + topic);
            msg.put("privateChannel", false);
            msg.put("response", true);
            String text = mapper.writeValueAsString(msg);
            LOGGER.debug("Sending subscription message: {}", text);
            client.send(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static PropertyPathValueResolver resolver = new PropertyPathValueResolver();

    private class WebSocketClient extends org.java_websocket.client.WebSocketClient
    {
        private final Map<String, Object> bullet;
        private final Integer pingInterval;
        private Timer timer;

        public WebSocketClient(Map<String, Object> bullet)
        {
            super(URI.create(resolver.get((Map<String, Object>) ((List) resolver.get(bullet, "data.instanceServers")).get(0), "endpoint")
                    + "?token=" + (String) resolver.get(bullet, "data.token") + "&connectId=" + ThreadLocalRandom.current().nextInt()));
            LOGGER.debug("Connection to {}", getURI());
            this.bullet = bullet;
            var m = (Map) ((List) resolver.get(bullet, "data.instanceServers")).get(0);
            this.pingInterval = (Integer) resolver.get(m, "pingInterval");
        }

        @Override
        public void onOpen(ServerHandshake handshake)
        {
            LOGGER.info("Connection opened");
            timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    ping();
                }
            }, pingInterval, pingInterval);
        }

        private void parseSafe(String message)
        {
            try {
                Map<String, Object> map = mapper.readValue(message, new TypeReference<>() {});
                String topic = resolver.get(map, "topic");
                Object data = resolver.get(map, "data");
                if (!"message".equals(resolver.get(map, "type"))
                        || !"trade.ticker".equals(resolver.get(map, "subject"))
                        || topic == null || !topic.startsWith("/market/ticker:")
                        || !(data instanceof Map)
                ) {
                    if ("welcome".equals(resolver.get(map, "type")))
                        subscribe();
                    else if ("ack".equals(resolver.get(map, "type")))
                        return; // Ack of subscription
                    else if ("pong".equals(resolver.get(map, "type")))
                        return; // Pong of ping
                    else
                        LOGGER.warn("Unhandled msg received in: {}", message);
                    return;
                }
                TokenPair pair = TokenPair.parse(topic.split(":")[1]);
                BigDecimal price = new BigDecimal((String) resolver.get((Map<String, Object>) data, "price"));
                Long time = resolver.get((Map<String, Object>) data, "time");
                TokenPairPrice tokenPrice = new TokenPairPrice(pair, price, Instant.ofEpochMilli(time));
                LOGGER.debug("Received data: {}", tokenPrice);
                cache.put(pair, tokenPrice);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        private void ping()
        {
            // FIXME what is this id??
            send("{" +
                    "\"id\":\"1545910590801\"," +
                    "\"type\":\"ping\"" +
                    "}");
        }

        @Override
        public void onMessage(String message)
        {
            parseSafe(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote)
        {
            LOGGER.warn("Connection closed with code {}, Remote: {}, Reason: {}", code, remote, reason);
            // TODO: FALLBAK?
            client = null;
            timer.cancel();
            timer = null;
            startWebSocket();
        }

        @Override
        public void onError(Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }
    }
}
