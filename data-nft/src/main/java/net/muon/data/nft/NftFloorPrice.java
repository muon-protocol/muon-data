package net.muon.data.nft;

import java.math.BigDecimal;
import java.time.Instant;

public class NftFloorPrice
{
    private final BigDecimal price;
    private final Long time;

    public NftFloorPrice(BigDecimal price, Long time)
    {
        this.price = price;
        this.time = time;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public Long getTime()
    {
        return time;
    }

    public Instant getFormattedTime()
    {
        return Instant.ofEpochSecond(time);
    }
}
