package net.muon.data.core;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.*;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Source<Q extends Quote>
{

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    protected final IgniteCache<String, Q> cache;
    protected final List<String> enabledExchanges;
    protected final List<String> symbols;
    protected final String id;
    protected boolean forceDisable = false;

    public Source(String id,
                  List<String> exchanges,
                  Ignite ignite,
                  List<String> symbols,
                  List<QuoteChangeListener> changeListeners,
                  Factory<ExpiryPolicy> cacheExpiryPolicy)
    {
        LOGGER.debug("Constructing {} with id {}", this.getClass(), id);
        var config = new CacheConfiguration<String, Q>(id + "_cache");
        config.setCacheMode(CacheMode.REPLICATED);
        config.setExpiryPolicyFactory(cacheExpiryPolicy);
        this.cache = ignite.getOrCreateCache(config);
        cache.registerCacheEntryListener(new CacheEntryListenerConfigurationImpl(changeListeners));
        this.symbols = symbols;
        this.id = id;
        this.enabledExchanges = exchanges;
    }

    public String getId()
    {
        return id;
    }

    public boolean isEnabled()
    {
        return !forceDisable && (enabledExchanges.contains("*") || enabledExchanges.contains(id));
    }

    public abstract void connect();//FIXME this method may return Completable (CompletableFuture)

    public abstract void disconnect();

    public Map<String, String> getInfo()
    {
        Map<String, String> info = new HashMap<>();
        info.put("id", id);
        return info;
    }

    public Q getQuote(String symbol)
    {
        return cache.get(symbol);
    }

    public Q load(String symbol)
    {
        return null;
    }

    public List<Q> getAll()
    {//FIXME Space limitation/ Bad api
        List<Q> all = new ArrayList<>();
        cache.forEach((Cache.Entry<String, Q> t) -> {
            all.add(getQuote(t.getKey()));//FIXME t.getValue returns object of type Quote
        });
        return all;
    }

    public void addQuote(Q q)
    {
        cache.put(q.getSymbol().toUpperCase(), q);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} added to {}'s cache. Cache size is {} ", q.getSymbol().toUpperCase(), id, cache.size());
        }
    }

    public void forceDisable()
    {
        forceDisable = true;
        disconnect();
    }

    @Override
    public String toString()
    {
        return id;
    }

    private class CacheEntryListenerConfigurationImpl implements CacheEntryListenerConfiguration
    {

        private final List<QuoteChangeListener> changeListeners;

        private CacheEntryListenerConfigurationImpl(List<QuoteChangeListener> changeListeners)
        {
            this.changeListeners = changeListeners;
        }

        @Override
        public Factory<CacheEntryListener> getCacheEntryListenerFactory()
        {
            return CacheEntryListenerImpl::new;

        }

        public class CacheEntryListenerImpl implements CacheEntryUpdatedListener, CacheEntryCreatedListener
        {

            private void publish(Iterable<CacheEntryEvent> events)
            {
                events.forEach(e -> changeListeners.forEach(l -> l.quoteUpdated((Quote) e.getValue())));
            }

            @Override
            public void onUpdated(Iterable events) throws CacheEntryListenerException
            {
                publish(events);
            }

            @Override
            public void onCreated(Iterable events) throws CacheEntryListenerException
            {
                publish(events);
            }
        }

        @Override
        public boolean isOldValueRequired()
        {
            return false;
        }

        @Override
        public Factory<CacheEntryEventFilter<? extends String, ? extends Q>> getCacheEntryEventFilterFactory()
        {
            return () -> (CacheEntryEventFilter<String, ? extends Q>) e -> e.getEventType() == EventType.CREATED || e.getEventType() == EventType.UPDATED;
        }

        @Override
        public boolean isSynchronous()
        {
            return false;
        }

    }

}
