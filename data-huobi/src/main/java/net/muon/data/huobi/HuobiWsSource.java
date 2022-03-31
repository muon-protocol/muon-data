package net.muon.data.huobi;

import info.bitrich.xchangestream.huobi.HuobiStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class HuobiWsSource extends AbstractXchangeSource
{
    public HuobiWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                         ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(HuobiStreamingExchange.class);
    }
}
