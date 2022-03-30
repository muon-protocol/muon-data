package net.muon.data.kraken;

import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.Executor;

public class KrakenWsSource extends AbstractXchangeSource
{
    public KrakenWsSource(Ignite ignite, List<TokenPair> subscriptionPairs, Executor executor,
                          String secret, String apiKey)
    {
        super("kraken", ignite, subscriptionPairs, apiKey, secret);
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
        connect(KrakenStreamingExchange.class);
    }
}
