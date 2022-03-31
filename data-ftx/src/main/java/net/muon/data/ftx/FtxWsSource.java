package net.muon.data.ftx;

import info.bitrich.xchangestream.ftx.FtxStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class FtxWsSource extends AbstractXchangeSource
{
    public FtxWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                       ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(FtxStreamingExchange.class);
    }
}
