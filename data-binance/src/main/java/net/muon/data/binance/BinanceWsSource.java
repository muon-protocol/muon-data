package net.muon.data.binance;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.incubator.TokenPair;
import net.muon.data.core.incubator.TokenPairPrice;
import net.muon.data.core.incubator.TokenPriceSource;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.concurrent.Executor;

public class BinanceWsSource extends CryptoSource implements TokenPriceSource
{
    public BinanceWsSource(Ignite ignite, List<String> exchanges, List<String> symbols, Executor executor,
                           String secret, String apiKey, List<QuoteChangeListener> changeListeners)
    {
        super("binance", exchanges, ignite, symbols, changeListeners, apiKey, secret, null);
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
        connect(BinanceStreamingExchange.class);
    }

    @Override
    protected void subscribe()
    {
        ((BinanceStreamingExchange) exchange).enableLiveSubscription();
        super.subscribe();
    }

    @Override
    public TokenPairPrice getTokenPairPrice(TokenPair pair)
    {
        CryptoQuote quote = getQuote(pair.toString());
        return new TokenPairPrice(pair, quote.getPrice(), quote.getInstantTime());
    }
}
