package net.muon.data.binance;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import net.muon.data.core.CryptoQuote;
import net.muon.data.core.CryptoSource;
import net.muon.data.core.QuoteChangeListener;
import org.apache.ignite.Ignite;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.exceptions.ExchangeException;

import java.io.IOException;
import java.util.List;

public class BinanceSource extends CryptoSource
{
    public BinanceSource(Ignite ignite, List<String> exchanges, List<String> symbols,
                         String secret, String apiKey, List<QuoteChangeListener> changeListeners)
    {
        super("binance", exchanges, ignite, symbols, changeListeners, apiKey, secret);
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
    public CryptoQuote load(String symbol)
    {
        try {
            var pair = new CurrencyPair(symbol);
            Trades trades = exchange.getMarketDataService().getTrades(pair);
            if (trades != null && !trades.getTrades().isEmpty())
                return new CryptoQuote(trades.getTrades().get(0));
        } catch (IllegalArgumentException ex) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            throw ex;
        } catch (ExchangeException ex) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            throw ex;
        } catch (IOException e) {
            LOGGER.warn("Exception suppressed", e);
        }
        return null;
    }
}
