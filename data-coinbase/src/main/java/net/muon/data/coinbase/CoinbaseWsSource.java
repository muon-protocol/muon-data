package net.muon.data.coinbase;

import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class CoinbaseWsSource extends AbstractXchangeSource {
    public CoinbaseWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                           ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(CoinbaseProStreamingExchange.class);
    }

}
