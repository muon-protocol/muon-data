package net.muon.data.core.dex;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.SubgraphService;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DexSource extends CryptoSource
{
    private final ObjectMapper mapper;
    private final SubgraphService subgraphService;
    private final IgniteCache<String, String> tokenAddressCache;

    public DexSource(String name, Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges,
                     List<String> symbols, List<QuoteChangeListener> changeListeners)
    {
        super(name, exchanges, ignite, symbols, changeListeners, null, null, CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 10))); // FIXME ?
        this.mapper = mapper;
        subgraphService = new SubgraphService(endpoint, HttpClient.newBuilder().build(), mapper);

        var config = new CacheConfiguration<String, String>(id + "_token_address_cache");
        config.setCacheMode(CacheMode.REPLICATED);
        tokenAddressCache = ignite.getOrCreateCache(config);
    }

    @Override
    public void connect()
    {}

    @Override
    public CryptoQuote getQuote(String symbol)
    {
        var q = super.getQuote(symbol);
        if (q != null)
            return q;
        q = new CryptoQuote(symbol, getPrice(symbol), Date.from(Instant.now()).getTime()); // FIXME
        super.addQuote(q);
        return q;
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

    private String fetchTokenAddress(String symbol)
    {
        var address = tokenAddressCache.get(symbol);
        if (address != null)
            return address;
        try {
            var response = subgraphService.fetchQueryResponse(getTokenAddressQuery(symbol));
            address = mapper.readValue(response, TokenAddressQueryResponse.class).getAddress();
            tokenAddressCache.put(symbol, address);
            return address;
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
