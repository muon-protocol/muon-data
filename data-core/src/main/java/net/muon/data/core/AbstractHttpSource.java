package net.muon.data.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;

public abstract class AbstractHttpSource implements TokenPriceSource
{
    protected abstract TokenPairPrice load(TokenPair pair);

    private final String id;
    private final LoadingCache<TokenPair, TokenPairPrice> cache;

    public AbstractHttpSource(String id)
    {
        this.id = id;
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMillis(500))
                .build(this::load);
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public final TokenPairPrice getTokenPairPrice(TokenPair pair)
    {
        return cache.get(pair);
    }
}
