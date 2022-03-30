package net.muon.data.gateio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import net.muon.data.core.PropertyPathValueResolver;
import net.muon.data.core.incubator.TokenPair;
import net.muon.data.core.incubator.TokenPairPrice;
import net.muon.data.core.incubator.TokenPriceSource;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.java_websocket.handshake.ServerHandshake;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;

public class GateioWsSource implements TokenPriceSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GateioWsSource.class);

    private final IgniteCache<TokenPair, TokenPairPrice> cache;
    private final List<TokenPair> subscriptionPairs;
    private final ObjectMapper mapper;
    private final Map<String, String> symbolDictionary = new HashMap<>();

    private WebSocketClient client;
    private boolean singleSubscriptionMode = false;

    public GateioWsSource(Ignite ignite, List<TokenPair> subscriptionPairs,
                          ObjectMapper objectMapper, Executor executor)
    {
        var cacheConfig = new CacheConfiguration<TokenPair, TokenPairPrice>("gateio_cache");
        cacheConfig.setCacheMode(CacheMode.REPLICATED);
        this.cache = ignite.getOrCreateCache(cacheConfig);

        this.subscriptionPairs = subscriptionPairs;
        this.mapper = objectMapper;
        subscriptionPairs.stream()
                .map(TokenPair::toString)
                .map(CurrencyPair::new)
                .forEach(pair -> symbolDictionary.put((pair.base.toString() + "_" + pair.counter.toString()).toUpperCase(), pair.base + "-" + pair.counter));

        executor.execute(() -> {
            try {
                connect();
            } catch (RuntimeException ex) {
                disconnect();
            }
        });
    }

    @Override
    public TokenPairPrice getTokenPairPrice(TokenPair pair)
    {
        return cache.get(pair);
    }

    public void connect()
    {
        LOGGER.debug("Symbols: {}", subscriptionPairs);
        startWebSocket();
    }

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
        // FIXME: What happens for the last thread when reconnects
        Preconditions.checkState(client == null, "Already started but not closed");
        client = new WebSocketClient();
        client.connect();
    }

    private List<TokenPairPrice> parseSafe(String message)
    {
        // TODO: EXCEPTION HANDLING (if protocol changes)
        GateioResponse response;
        try {
            response = mapper.readValue(message, GateioResponse.class);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to parse event: " + message, e);
            return Collections.emptyList();
        }
        if ("spot.pong".equals(response.getChannel())) {
            return Collections.emptyList();
        }

        if (!"spot.trades".equals(response.getChannel())) {
            LOGGER.warn("Unexpected channel received in: {}", message);
            return Collections.emptyList();
        }

        if ("subscribe".equals(response.getEvent())) {
            if (response.getError() != null) {
                LOGGER.warn("Gateio subscription failed with error: {}", response.getError());
                if (response.getError().getCode() == 2) {
                    if (singleSubscriptionMode) return Collections.emptyList();
                    String errorMessage = response.getError().getMessage();
                    String errPair = null;
                    for (String pair : symbolDictionary.keySet()) {
                        if (errorMessage.contains(pair)) {
                            errPair = pair;
                        }
                    }

                    if (errPair == null) {
                        LOGGER.warn("Failed to extract pair from: {}; Switching to single subscription mode.", response.getError());
                        singleSubscriptionMode = true;
                    } else {
                        symbolDictionary.remove(errPair);
                    }
                    subscribe();
                    return Collections.emptyList();
                }
                if (!singleSubscriptionMode) subscribe();
                return Collections.emptyList();
            }
            if (response.getResult() != null) {
                if ("success".equals(response.getResult().getStatus())) {
                    LOGGER.debug("Subscription succeeded for: {}", symbolDictionary.keySet());
                    return Collections.emptyList();
                }
                LOGGER.warn("Unexpected response received: {}", message);
                return Collections.emptyList();
            }
        }

        if (!"update".equals(response.getEvent())) {
            LOGGER.warn("Unexpected event received in: {}", message);
            return Collections.emptyList();
        }

        var result = response.getResult();
        if (result == null) {
            LOGGER.warn("Unexpected event received in: {}", message);
            return Collections.emptyList();
        }

        TokenPair pair = TokenPair.parse(symbolDictionary.get(result.getPair().toUpperCase()));
        TokenPairPrice tokenPrice = new TokenPairPrice(pair, result.getPrice(),
                Instant.ofEpochMilli(result.getTime().longValue()));

        return Collections.singletonList(tokenPrice);
    }

    protected void subscribe()
    {
        if (singleSubscriptionMode) {
            for (String symbol : symbolDictionary.keySet()) {
                try {
                    doSubscribe(Collections.singletonList(symbol));
                } catch (Exception e) {
                    LOGGER.warn("Exception suppressed", e);
                }
            }
        }
        doSubscribe(symbolDictionary.keySet());
    }

    private void doSubscribe(Collection<String> symbols)
    {
        try {
            String text = mapper.writeValueAsString(new GateioSubscriptionMessage(symbols));
            LOGGER.debug("Sending subscription message: {}", text);
            client.send(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static PropertyPathValueResolver resolver = new PropertyPathValueResolver();

    private class WebSocketClient extends org.java_websocket.client.WebSocketClient
    {
        private final Integer pingInterval;
        private Timer timer;

        public WebSocketClient()
        {
            super(URI.create("wss://api.gateio.ws/ws/v4/"));
            LOGGER.debug("Connection to {}", getURI());
            this.pingInterval = 5000;

        }

        @Override
        public void onOpen(ServerHandshake handshake)
        {
            LOGGER.info("Connection opened");
            subscribe();
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

        private void ping()
        {
            send("{\"time\": " + new Date().getTime() / 1000 + ", \"channel\" : \"spot.ping\"}");
        }

        @Override
        public void onMessage(String message)
        {
            var prices = parseSafe(message);
            LOGGER.debug("Received data: {}", prices);
            prices.forEach(price -> cache.put(price.pair(), price));
        }

        @Override
        public void onClose(int code, String reason, boolean remote)
        {
            LOGGER.warn("Connection closed with code {}, Remote: {}, Reason: {}", code, remote, reason);
            // TODO: FALLBAK?
            client = null;
            startWebSocket();
        }

        @Override
        public void onError(Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }
    }
}
