package net.muon.data.core;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.exceptions.ExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractXchangeSource implements TokenPriceSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXchangeSource.class);

    protected final IgniteCache<TokenPair, TokenPairPrice> cache;
    protected final String id;
    protected final List<TokenPair> subscriptionPairs;
    protected final String apiKey;
    protected final String secret;

    protected StreamingExchange exchange;
    protected ProductSubscription subscription;
    protected Disposable subscriptionDisposable;

    public AbstractXchangeSource(String id, Ignite ignite, List<TokenPair> subscriptionPairs, String apiKey, String secret)
    {
        var config = new CacheConfiguration<TokenPair, TokenPairPrice>(id + "_cache");
        config.setCacheMode(CacheMode.REPLICATED);
        this.cache = ignite.getOrCreateCache(config);

        this.id = id;
        this.subscriptionPairs = subscriptionPairs;
        this.apiKey = apiKey;
        this.secret = secret;
    }

    @Override
    public TokenPairPrice getTokenPairPrice(TokenPair pair)
    {
        return cache.get(pair);
    }

    public abstract void connect();

    protected void connect(Class<? extends StreamingExchange> clazz)
    {
        exchange = createExchange(clazz);
        subscription = createSubscription();
        doConnect();
    }

    protected void doConnect()
    {
        Completable connection = exchange.connect(subscription);
        try {
            exchange.connectionSuccess().subscribe(s -> {
                LOGGER.debug("exchange {} connection success", id);
            });
        } catch (Exception e) {
        }
        exchange.connectionStateObservable().subscribe(s -> {
            LOGGER.debug("exchange connection state changed to {} for {}", s, id);
            if (s == ConnectionStateModel.State.OPEN) {
                subscribe();
            }
        });
        subscriptionDisposable = connection.subscribe();
    }

    protected StreamingExchange createExchange(Class<? extends StreamingExchange> clazz)
    {
        return ExchangeFactory.INSTANCE.createExchange(clazz, apiKey, secret);
    }

    protected ProductSubscription createSubscription()
    {
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        if (subscriptionPairs.isEmpty()) {
            try {
                exchange.getExchangeSymbols().forEach(builder::addTrades);
            } catch (Exception e) {
                LOGGER.warn("Exception suppressed.", e);
            }
        } else {
            try {
                subscriptionPairs.forEach(pair -> {
                    try {
                        builder.addTrades(new CurrencyPair(pair.toString()));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Failed to convert symbol {} into currency. Reason: {}", pair, e.getMessage());
                    } catch (Exception e) {
                        LOGGER.warn("Exception suppressed.", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.warn("Exception suppressed.", e);
            }
        }
        return builder.build();
    }

    public void disconnect()
    {
        try {
            if (subscriptionDisposable != null) {
                subscriptionDisposable.dispose();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to dispose subscription", e);
        }
        try {
            if (exchange != null) {
                exchange.disconnect();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to disconnect", e);
        }
    }

    protected void subscribe()
    {
        LOGGER.info("{} is trying to subscribe on {}", id, subscription.getTrades());
        subscription.getTrades().forEach(currencyPair -> {
            try {
//                if (!exchange.getExchangeSymbols().contains(currencyPair)) {
//                    LOGGER.warn("{} does not support currency pair {}", getId(), currencyPair);
//                    return;
//                }
                Disposable d = exchange.getStreamingMarketDataService()
                        .getTrades(currencyPair)
                        .subscribe(trade -> {
                            LOGGER.debug("Trade received: {}", trade);
                            TokenPair pair = new TokenPair(trade.getCurrencyPair().base.toString(), trade.getCurrencyPair().counter.toString());
                            TokenPairPrice price = new TokenPairPrice(pair, trade.getPrice(), trade.getTimestamp().toInstant());
                            cache.put(pair, price);
                        }, throwable -> LOGGER.error("Error in trade {} subscription: {}", currencyPair, throwable.getMessage()));
            } catch (ExchangeException e) {
                LOGGER.warn("ExchangeException suppressed", e);
            }
        });
    }

}
