package net.muon.data.hitbtc;

import info.bitrich.xchangestream.hitbtc.HitbtcStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class HitbtcWsSource extends AbstractXchangeSource
{
    public HitbtcWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                          ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(HitbtcStreamingExchange.class);
    }
}
