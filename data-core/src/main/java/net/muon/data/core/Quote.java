package net.muon.data.core;

import java.math.BigDecimal;

public class Quote
{
    private String symbol;
    private BigDecimal price;
    private long time;//in millis
    private MarketStatus status;

    public Quote()
    {
    }

    public Quote(String symbol, BigDecimal price, long time, MarketStatus status)
    {
        this.symbol = symbol;
        this.price = price;
        this.time = time;
        this.status = status;
    }

    public MarketStatus getStatus()
    {
        return status;
    }

    public void setStatus(MarketStatus status)
    {
        this.status = status;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    @Override
    public String toString()
    {
        return "Quote{"
                + "symbol='" + symbol + '\''
                + ", price=" + price
                + ", time=" + time
                + ", status=" + status
                + '}';
    }

    public enum MarketStatus
    {
        PRE_MARKET,
        REGULAR_MARKET,
        POST_MARKET,
        EXTENDED_HOURS_MARKET,
        CLOSED,
        UNRECOGNIZED,
    }
}
