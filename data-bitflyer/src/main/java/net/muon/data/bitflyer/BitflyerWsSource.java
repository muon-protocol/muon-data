package net.muon.data.bitflyer;

import info.bitrich.xchangestream.bitflyer.BitflyerStreamingExchange;
import net.muon.data.core.AbstractXchangeSource;
import net.muon.data.core.TokenPair;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BitflyerWsSource extends AbstractXchangeSource
{
    public BitflyerWsSource(String id, List<TokenPair> subscriptionPairs, String secret, String apiKey,
                            ExecutorService executor, Ignite ignite)
    {
        super(id, subscriptionPairs, apiKey, secret, executor, ignite);
    }

    @Override
    public void connect()
    {
        connect(BitflyerStreamingExchange.class);
    }
}
