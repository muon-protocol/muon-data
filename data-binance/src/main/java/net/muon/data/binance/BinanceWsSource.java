package net.muon.data.binance;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.Executor;

public class BinanceWsSource extends AbstractXchangeSource
{
    public BinanceWsSource(Ignite ignite, List<TokenPair> subscriptionPairs, Executor executor,
                           String secret, String apiKey)
    {
        super("binance", ignite, subscriptionPairs, apiKey, secret);
        executor.execute(() -> {
            try {
                connect();
            } catch (RuntimeException ex) {
                disconnect();
            }
        });
    }

    @Override
    public void connect()
    {
        connect(BinanceStreamingExchange.class);
    }

    @Override
    protected void subscribe()
    {
        ((BinanceStreamingExchange) exchange).enableLiveSubscription();
        super.subscribe();
    }
}
