package net.muon.data.kraken;

import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import org.apache.ignite.Ignite;

import java.util.List;

public class KrakenSource extends CryptoSource
{

    public KrakenSource(Ignite ignite, List<String> exchanges, List<String> symbols,
                        String secret, String apiKey, List<QuoteChangeListener> changeListeners)
    {
        super("kraken", exchanges, ignite, symbols, changeListeners, apiKey, secret, null);
    }

    @Override
    public void connect()
    {
        connect(KrakenStreamingExchange.class);
    }

}
