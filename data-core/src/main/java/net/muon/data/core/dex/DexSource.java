package net.muon.data.core.dex;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.SubgraphService;
import org.apache.ignite.Ignite;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class DexSource extends CryptoSource
{
    private final ObjectMapper mapper;
    private final SubgraphService subgraphService;
    private final Map<String, String> tokens;

    public DexSource(String name, Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges,
                     Map<String, String> tokens, List<QuoteChangeListener> changeListeners)
    {
        super(name, exchanges, ignite, Collections.emptyList(), changeListeners, null, null, CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 10))); // FIXME ?
        this.mapper = mapper;
        subgraphService = new SubgraphService(endpoint, HttpClient.newBuilder().build(), mapper);
        this.tokens = tokens;
    }

    @Override
    public void connect() {}

    @Override
    public CryptoQuote getQuote(String symbol)
    {
        var q = super.getQuote(symbol);
        if (q != null)
            return q;
        var price = getPrice(symbol);
        if (price == null)
            return null;
        q = new CryptoQuote(symbol, price, Date.from(Instant.now()).getTime()); // FIXME
        super.addQuote(q);
        return q;
    }

    private BigDecimal getPrice(String symbol)
    {
        var parts = symbol.split("-");
        if (parts.length != 2)
            throw new IllegalArgumentException("Could not parse currency pair from '" + symbol + "'");
        var token0Address = getTokenAddress(parts[0]);
        if (token0Address == null)
            return null;
        var token1Address = getTokenAddress(parts[1]);
        if (token1Address == null)
            return null;
        return getPrice(token0Address, token1Address);
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

    private String getTokenAddress(String symbol)
    {
        return tokens.get(symbol);
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
