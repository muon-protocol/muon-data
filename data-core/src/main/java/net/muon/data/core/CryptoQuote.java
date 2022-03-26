package net.muon.data.core;

import org.knowm.xchange.dto.marketdata.Trade;

import java.math.BigDecimal;

public class CryptoQuote extends Quote
{
    public CryptoQuote(Quote q)
    {
        this(q.getSymbol(), q.getPrice(), q.getTime());
    }

    public CryptoQuote()
    {
    }

    public CryptoQuote(String symbol, BigDecimal price, long time)
    {
        super(symbol, price, time);
    }

    public CryptoQuote(Trade trade)
    {
        this(trade.getCurrencyPair().base + "-" + trade.getCurrencyPair().counter,
                trade.getPrice(), trade.getTimestamp().getTime());
    }

    @Override
    public String toString()
    {
        return "CryptoQuote{"
                + "symbol='" + getSymbol() + '\''
                + ", price=" + getPrice()
                + ", time=" + getTime()
                + '}';
    }
}
