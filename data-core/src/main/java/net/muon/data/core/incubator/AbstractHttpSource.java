package net.muon.data.core.incubator;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;

public abstract class AbstractHttpSource implements HttpSource
{
    protected abstract TokenPairPrice load(TokenPair pair);

    private final LoadingCache<TokenPair, TokenPairPrice> cache;

    public AbstractHttpSource()
    {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMillis(500))
                .build(this::load);
    }

    @Override
    public TokenPairPrice getTokenPairPrice(TokenPair pair)
    {
        return cache.get(pair);
    }
}
