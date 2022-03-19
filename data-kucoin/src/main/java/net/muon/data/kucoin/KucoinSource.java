package net.muon.data.kucoin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import net.muon.data.core.*;
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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class KucoinSource extends CryptoSource
{
    private WebSocketClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(KucoinSource.class);
    private final ObjectMapper mapper;
    private boolean singleSubscriptionMode = false;

    public KucoinSource(Ignite ignite, ObjectMapper mapper, List<String> exchanges,
                        List<String> symbols, List<QuoteChangeListener> changeListeners)
    {
        super("kucoin", exchanges, ignite, symbols, changeListeners, null, null, null);
        this.mapper = mapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CryptoQuote load(String symbol)
    {
        return null;
    }

    @Override
    public void connect()
    {
        LOGGER.debug("Symbols: {}", symbols);
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

    @Override
    public Map<String, String> getInfo()
    {
        Map<String, String> info = new HashMap<>();
        info.put("id", id);
        info.put("name", "kucoin");
//        info.put("wss-uri", URI_BUILDER.build().toUri().toString());
        return info;
    }

    @Override
    protected void subscribe()
    {
        if (singleSubscriptionMode) {
            for (String symbol : symbols) {
                try {
                    doSubscribe(Collections.singletonList(symbol));
                } catch (Exception e) {
                    LOGGER.warn("Exception suppressed", e);
                }
            }
        }
        doSubscribe(symbols);
    }

    private void doSubscribe(List<String> symbols)
    {
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("id", ThreadLocalRandom.current().nextInt());
            msg.put("type", "subscribe");
            msg.put("topic", "/market/ticker:" + String.join(",", symbols).toUpperCase());
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
                Map<String, Object> map = mapper.readValue(message, Map.class);
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
                var pair = topic.split(":")[1];
                var quote = new CryptoQuote();
                quote.setPrice(new BigDecimal((String) resolver.get((Map<String, Object>) data, "price")));
                quote.setTime(resolver.get((Map<String, Object>) data, "time"));
                quote.setSymbol(pair.toUpperCase());
                quote.setStatus(Quote.MarketStatus.REGULAR_MARKET);
                var quotes = Collections.singletonList(quote);
                LOGGER.debug("Received data: {}", quotes);
                quotes.forEach(KucoinSource.this::addQuote);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        private void ping()
        {
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
