package net.muon.data.core.dex;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.SubgraphService;
import org.apache.ignite.Ignite;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

public abstract class DexSource extends CryptoSource
{
    private final ObjectMapper mapper;
    private final SubgraphService subgraphService;

    public DexSource(String name, Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges,
                     List<String> symbols, List<QuoteChangeListener> changeListeners)
    {
        super(name, exchanges, ignite, symbols, changeListeners, null, null);
        this.mapper = mapper;
        subgraphService = new SubgraphService(endpoint, HttpClient.newBuilder().build(), mapper);
    }

    @Override
    public void connect()
    {}

    @Override
    public CryptoQuote getQuote(String symbol)
    {
        symbol = symbol.toUpperCase(Locale.ROOT);
        var price = getPrice(symbol);
        return new CryptoQuote(symbol, price, Instant.now().toEpochMilli()); // FIXME ?
    }

    private BigDecimal getPrice(String symbol)
    {
        var parts = symbol.split("-");
        if (parts.length != 2)
            throw new IllegalArgumentException("Could not parse currency pair from '" + symbol + "'");
        return getPrice(fetchTokenAddress(parts[0]), fetchTokenAddress(parts[1]));
    }

    private BigDecimal getPrice(String base, String counter)
    {
        var token0PriceRequested = base.compareTo(counter) < 0;
        var token0 = token0PriceRequested ? base : counter;
        var token1 = token0PriceRequested ? counter : base;
        return fetchTokenPrice(token0, token1, token0PriceRequested);
    }

    private BigDecimal fetchTokenPrice(String token0, String token1, boolean token0PriceRequested)
    {
        try {
            var response = subgraphService.fetchQueryResponse(getTokenPriceQuery(token0, token1));
            var queryResponse = mapper.readValue(response, PairReservesQueryResponse.class);
            return token0PriceRequested ? queryResponse.getToken0Price() : queryResponse.getToken1Price();
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String fetchTokenAddress(String symbol) // TODO: cache? reuse?
    {
        try {
            var response = subgraphService.fetchQueryResponse(getTokenAddressQuery(symbol));
            return mapper.readValue(response, TokenAddressQueryResponse.class).getAddress();
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getTokenPriceQuery(String token0, String token1)
    {
        return String.format("{\n" +
                "  pairs(first: 1, where: {token0: \"%s\", token1: \"%s\"}){\n" +
                "    reserve0\n" +
                "    reserve1\n" +
                "  }\n" +
                "}", token0, token1);
    }

    protected abstract String getTokenAddressQuery(String symbol);
}
