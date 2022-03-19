package net.muon.data.gateio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import net.muon.data.core.*;
import org.apache.ignite.Ignite;
import org.java_websocket.handshake.ServerHandshake;
import org.knowm.xchange.currency.CurrencyPair;
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

public class GateioSource extends CryptoSource
{
    private WebSocketClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(GateioSource.class);
    private final ObjectMapper mapper;
    private final Map<String, String> symbolDictionary = new HashMap<>();
    private boolean singleSubscriptionMode = false;

    public GateioSource(Ignite ignite,
                        ObjectMapper mapper,
                        List<String> exchanges,
                        List<String> symbols,
                        List<QuoteChangeListener> changeListeners)
    {
        super("gateio", exchanges, ignite, symbols, changeListeners, null, null, null);
        this.mapper = mapper;
        symbols.stream()
                .map(CurrencyPair::new)
                .forEach(pair -> symbolDictionary.put((pair.base.toString() + "_" + pair.counter.toString()).toUpperCase(), pair.base + "-" + pair.counter));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CryptoQuote load(String symbol)
    {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.gateio.ws/api/v4/spot/trades?currency_pair=" + symbol.replace('-', '_') + "&limit=1"))
                    .GET()
                    .build();
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            List list = mapper.readValue(response.body(), new TypeReference<>() {});
            if (list == null || list.isEmpty())
                return null;
            Map<String, Object> result = (Map<String, Object>) list.get(0);
            BigDecimal price = (BigDecimal) result.get("price");
            long time = (long) result.get("create_time_ms");
            return new CryptoQuote(symbol, price, time);
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
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
        // FIXME: What happens for the last thread when reconnects
        Preconditions.checkState(client == null, "Already started but not closed");
        client = new WebSocketClient();
        client.connect();
    }

    private List<CryptoQuote> parseSafe(String message)
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

        var quote = new CryptoQuote();
        quote.setPrice(result.getPrice());
        quote.setTime(result.getTime().longValue());
        quote.setSymbol(symbolDictionary.get(result.getPair().toUpperCase()));
        quote.setStatus(Quote.MarketStatus.REGULAR_MARKET);
        return Collections.singletonList(quote);
    }

    @Override
    public Map<String, String> getInfo()
    {
        Map<String, String> info = new HashMap<>();
        info.put("id", id);
        info.put("name", "Gateio");
        return info;
    }

    @Override
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
            var qoutes = parseSafe(message);
            LOGGER.debug("Received data: {}", qoutes);
            qoutes.forEach(GateioSource.this::addQuote);
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
