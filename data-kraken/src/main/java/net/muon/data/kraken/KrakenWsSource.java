package net.muon.data.kraken;

import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class KrakenWsSource extends AbstractXchangeSource
{
    public KrakenWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                          ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(KrakenStreamingExchange.class);
    }
}
