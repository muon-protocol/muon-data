package net.muon.data.core;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class AbstractWsSource implements TokenPriceSource
{
    protected final String id;
    private final ExecutorService executor;
    protected final IgniteCache<TokenPair, TokenPairPrice> cache;
    protected final List<TokenPair> subscriptionPairs;

    protected AbstractWsSource(String id, List<TokenPair> subscriptionPairs, ExecutorService executor, Ignite ignite)
    {
        this.id = id;
        this.subscriptionPairs = subscriptionPairs;
        this.executor = executor;

        var cacheConfig = new CacheConfiguration<TokenPair, TokenPairPrice>(getId() + "_cache");
        cacheConfig.setCacheMode(CacheMode.REPLICATED);
        this.cache = ignite.getOrCreateCache(cacheConfig);
    }

    @Override
    public TokenPairPrice getTokenPairPrice(TokenPair pair)
    {
        return cache.get(pair);
    }

    @Override
    public String getId()
    {
        return id;
    }

    public abstract void connect();

    public abstract void disconnect();
}
