package net.muon.data.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import org.apache.ignite.Ignite;
import org.java_websocket.handshake.ServerHandshake;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class GeminiSource extends CryptoSource
{
    private WebSocketClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiSource.class);
    private final ObjectMapper mapper;
    private final Map<String, String> symbolDictionary = new HashMap<>();

    private final List<String> supportedPairs = List.of(
            "btcusd", "ethbtc", "ethusd", "zecusd",
            "zecbtc", "zeceth", "zecbch", "zecltc",
            "bchusd", "bchbtc", "bcheth", "ltcusd",
            "ltcbtc", "ltceth", "ltcbch", "batusd",
            "daiusd", "linkusd", "oxtusd", "batbtc",
            "linkbtc", "oxtbtc", "bateth", "linketh",
            "oxteth", "ampusd", "compusd", "paxgusd",
            "mkrusd", "zrxusd", "kncusd", "manausd",
            "storjusd", "snxusd", "crvusd", "balusd",
            "uniusd", "renusd", "umausd", "yfiusd",
            "btcdai", "ethdai", "aaveusd", "filusd",
            "btceur", "btcgbp", "etheur", "ethgbp",
            "btcsgd", "ethsgd", "sklusd", "grtusd",
            "bntusd", "1inchusd", "enjusd", "lrcusd",
            "sandusd", "cubeusd", "lptusd", "bondusd",
            "maticusd", "injusd", "sushiusd", "dogeusd",
            "alcxusd", "mirusd", "ftmusd", "ankrusd",
            "btcgusd", "ethgusd", "ctxusd", "xtzusd",
            "axsusd", "slpusd", "lunausd", "ustusd",
            "mco2usd", "dogebtc", "dogeeth", "wcfgusd",
            "rareusd", "radusd", "qntusd", "nmrusd",
            "maskusd", "fetusd", "ashusd", "audiousd",
            "api3usd", "usdcusd", "shibusd", "rndrusd",
            "mcusd", "galausd", "ensusd", "kp3rusd",
            "cvcusd", "elonusd", "mimusd", "spellusd",
            "tokeusd", "ldousd", "rlyusd", "solusd",
            "rayusd", "sbrusd", "apeus"
    );


    public GeminiSource(Ignite ignite, ObjectMapper mapper, List<String> exchanges, List<String> symbols,
                        List<QuoteChangeListener> changeListeners)
    {
        super("gemini", exchanges, ignite, symbols, changeListeners, null, null, null);
        this.mapper = mapper;
        symbols.stream()
                .map(CurrencyPair::new)
                .forEach(pair -> {
                    String pairRep = pair.base + "" + pair.counter;
                    if (supportedPairs.contains(pairRep.toLowerCase())) {
                        symbolDictionary.put(
                                (pair.base.toString() + pair.counter.toString()).toLowerCase(),
                                pairRep.toUpperCase());
                    } else {
                        LOGGER.warn("Unsupported pair:{}", pair);
                    }
                });
    }

    @Override
    public void connect()
    {
        if (symbolDictionary.size() == 0) {
            LOGGER.warn("No supported pairs found for gemini");
            return;
        }
        LOGGER.debug("Symbols: {}", symbolDictionary.keySet());
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
        GeminiResponse response;
        LOGGER.debug(message);
        try {
            response = mapper.readValue(message, GeminiResponse.class);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to parse event: " + message, e);
            return Collections.emptyList();
        }
        if (!"update".equals(response.getType())) {
            LOGGER.warn("Unexpected response type received in: {}", message);
            return Collections.emptyList();
        }

        var quotes = new ArrayList<CryptoQuote>();
        for (var event : response.getEvents()) {
            if (!"change".equals(event.getType())) {
                LOGGER.warn("Unexpected event type received in: {}", event);
                continue;
            }

            String symbol;
            if (event.getSymbol() != null) {
                symbol = symbolDictionary.get(event.getSymbol().toLowerCase());
            } else {
                symbol = symbolDictionary.values().stream().findFirst().get();
            }

            var quote = new CryptoQuote(symbol, event.getPrice(), response.getTime());

            if (quote.getSymbol() == null) {
                LOGGER.warn("Unexpected event symbol received in: {}", event);
            } else {
                quotes.add(quote);
            }
        }

        return quotes;
    }

    @Override
    public Map<String, String> getInfo()
    {
        Map<String, String> info = new HashMap<>();
        info.put("id", id);
        info.put("name", "Gemini");
        return info;
    }

    private class WebSocketClient extends org.java_websocket.client.WebSocketClient
    {
        public WebSocketClient()
        {
            super(URI.create("wss://api.gemini.com/v1/" +
                    (symbolDictionary.size() > 1 ? "multimarketdata?symbols=" : "marketdata")
                    + String.join(",", symbolDictionary.values()) +
                    "?top_of_book=true" +
                    "?bids=false" +
                    "?offers=false" +
                    "?auctions=false" +
                    "?trades=false"
            ));
        }

        @Override
        public void onOpen(ServerHandshake handshake)
        {
            LOGGER.info("Connection opened");
        }

        @Override
        public void onMessage(String message)
        {
            var quotes = parseSafe(message);
            LOGGER.debug("Received data: {}", quotes);
            quotes.forEach(GeminiSource.this::addQuote);
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
