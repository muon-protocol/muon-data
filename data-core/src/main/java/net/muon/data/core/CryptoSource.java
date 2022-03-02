package net.muon.data.core;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import org.apache.ignite.Ignite;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.utils.ObjectMapperHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class CryptoSource extends Source<CryptoQuote>
{
    protected final String apiKey;
    protected final String secret;
    protected StreamingExchange exchange;
    protected ProductSubscription subscription;
    protected Disposable subsciptionDisposable;

    public CryptoSource(String id,
                        Optional<List<String>> exchanges,
                        Ignite ignite,
                        Optional<List<String>> symbols,
                        List<QuoteChangeListener> changeListeners,
                        String apiKey,
                        String secret)
    {
        super(id,
                exchanges.isPresent() ? exchanges.get() : Collections.EMPTY_LIST,
                ignite,
                symbols.isPresent() ? symbols.get() : Collections.EMPTY_LIST,
                changeListeners);
        this.apiKey = apiKey;
        this.secret = secret;
    }

    @Override
    public CryptoQuote getQuote(String symbol)
    {
        Quote q = super.getQuote(symbol);
        return q == null ? null : new CryptoQuote(q);
    }

    @Override
    public CryptoQuote load(String symbol)
    {
        return super.load(symbol);
    }

    protected void connect(Class<? extends StreamingExchange> clazz)
    {
        exchange = createExchange(clazz);
        subscription = createSubsciption();
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
        subsciptionDisposable = connection.subscribe();
    }

    protected StreamingExchange createExchange(Class<? extends StreamingExchange> clazz)
    {
        return ExchangeFactory.INSTANCE.createExchange(clazz, apiKey, secret);
    }

    protected ProductSubscription createSubsciption()
    {
        ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
        if (symbols.isEmpty()) {
            try {
                exchange.getExchangeSymbols().forEach(currencyPair -> builder.addTrades(currencyPair));
            } catch (Exception e) {
                LOGGER.warn("Exception suppressed.", e);
            }
        } else {
            try {
                symbols.forEach(currencyPair -> {
                    try {
                        builder.addTrades(new CurrencyPair(currencyPair));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Failed to convert symbol {} into currency. Reason: {}", currencyPair, e.getMessage());
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

    @Override
    public Map<String, String> getInfo()
    {
        Map<String, String> info = super.getInfo();
        info.put("name", this.exchange.getDefaultExchangeSpecification().getExchangeName());
        info.put("description", this.exchange.getDefaultExchangeSpecification().getExchangeDescription());
        info.put("uri", this.exchange.getDefaultExchangeSpecification().getPlainTextUri());
        info.put("ssl-uri", this.exchange.getDefaultExchangeSpecification().getSslUri());
        String meta = ObjectMapperHelper.toCompactJSON(this.exchange.getExchangeMetaData());
        info.put("meta-data", meta);
        return info;
    }

    @Override
    public void disconnect()
    {
        try {
            if (subsciptionDisposable != null) {
                subsciptionDisposable.dispose();
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
                            CryptoQuote cryptoQuote = new CryptoQuote(trade);
                            addQuote(cryptoQuote);
                        }, throwable -> LOGGER.error("Error in trade {} subscription: {}", currencyPair, throwable.getMessage()));
            } catch (ExchangeException e) {
                LOGGER.warn("ExchangeException suppressed", e);
            }
        });
    }

}
