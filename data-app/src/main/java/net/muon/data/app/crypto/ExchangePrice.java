package net.muon.data.app.crypto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.muon.data.core.incubator.TokenPairPrice;

import java.math.BigDecimal;
import java.time.Instant;

public class ExchangePrice
{
    private final Exchange exchange;
    private final BigDecimal price;
    private final Instant instant;

    public ExchangePrice(Exchange exchange, TokenPairPrice price)
    {
        this.exchange = exchange;
        this.price = price.price();
        this.instant = price.time();
    }

    public Exchange getExchange()
    {
        return exchange;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    @JsonIgnore
    public Instant getInstant()
    {
        return instant;
    }

    public Long getTime()
    {
        return instant.toEpochMilli();
    }

    public String getFormattedTime()
    {
        return instant.toString();
    }
}
