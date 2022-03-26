package net.muon.data.core;

import java.math.BigDecimal;
import java.time.Instant;

public class Quote
{
    private String symbol;
    private BigDecimal price;
    private long time;//in millis

    public Quote()
    {
    }

    public Quote(String symbol, BigDecimal price, long time)
    {
        this.symbol = symbol;
        this.price = price;
        this.time = time;
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

    public Instant getInstantTime()
    {
        return Instant.ofEpochMilli(this.time);
    }

    @Override
    public String toString()
    {
        return "Quote{"
                + "symbol='" + symbol + '\''
                + ", price=" + price
                + ", time=" + time
                + '}';
    }
}
