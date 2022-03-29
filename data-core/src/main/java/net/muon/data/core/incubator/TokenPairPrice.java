package net.muon.data.core.incubator;

import java.math.BigDecimal;
import java.time.Instant;

public final class TokenPairPrice
{
    private final TokenPair pair;
    private final BigDecimal price;
    private final Instant time;

    public TokenPairPrice(TokenPair pair, BigDecimal price, Instant time)
    {
        this.pair = pair;
        this.price = price;
        this.time = time;
    }

    public TokenPair pair()
    {
        return pair;
    }

    public BigDecimal price()
    {
        return price;
    }

    public Instant time()
    {
        return time;
    }

    @Override
    public String toString()
    {
        return "TokenPairPrice{" +
                "pair=" + pair +
                ", price=" + price +
                ", time=" + time +
                '}';
    }
}
