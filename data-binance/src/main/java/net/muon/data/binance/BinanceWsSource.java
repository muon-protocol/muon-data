package net.muon.data.binance;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class BinanceWsSource extends AbstractXchangeSource
{
    public BinanceWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                           ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
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
