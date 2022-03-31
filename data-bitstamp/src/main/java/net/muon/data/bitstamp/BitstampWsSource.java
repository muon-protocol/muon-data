package net.muon.data.bitstamp;

import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BitstampWsSource extends AbstractXchangeSource
{
    public BitstampWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                            ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(BitstampStreamingExchange.class);
    }
}
