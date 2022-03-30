package net.muon.data.binance;

import net.muon.data.core.AbstractHttpSource;
import net.muon.data.core.TokenPair;
import net.muon.data.core.TokenPairPrice;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;

import java.io.IOException;
import java.time.Instant;

public class BinanceHttpSource extends AbstractHttpSource
{
    private BinanceExchange exchange;

    public BinanceHttpSource(String id)
    {
        super(id);
    }

    public void init()
    {
        this.exchange = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class);
    }

    @Override
    protected TokenPairPrice load(TokenPair pair)
    {
        CurrencyPair currencyPair = new CurrencyPair(pair.token0(), pair.token1());
        try {
            Trades trades = exchange.getMarketDataService().getTrades(currencyPair);
            if (trades != null && !trades.getTrades().isEmpty()) {
                Trade trade = trades.getTrades().get(0);
                return new TokenPairPrice(pair, trade.getPrice(), Instant.ofEpochMilli(trade.getTimestamp().getTime()));
            }
            return null;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
