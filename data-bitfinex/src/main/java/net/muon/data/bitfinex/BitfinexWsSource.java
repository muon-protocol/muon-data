package net.muon.data.bitfinex;

import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BitfinexWsSource extends AbstractXchangeSource
{
    public BitfinexWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                            ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(BitfinexStreamingExchange.class);
    }
}
