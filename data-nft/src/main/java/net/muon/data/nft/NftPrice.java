package net.muon.data.nft;

import java.math.BigDecimal;
import java.time.Instant;

public class NftPrice
{
    private final BigDecimal latestPrice;
    private final Long latestPriceTime;
//    private final String latestPriceToken;
    private final BigDecimal averagePrice;
    private final Integer count;

    public NftPrice(BigDecimal latestPrice, Long latestPriceTime, BigDecimal averagePrice, Integer count)
    {
        this.latestPrice = latestPrice;
        this.latestPriceTime = latestPriceTime;
        this.averagePrice = averagePrice;
        this.count = count;
    }

    public BigDecimal getLatestPrice()
    {
        return latestPrice;
    }

    public Long getLatestPriceTime()
    {
        return latestPriceTime;
    }

    public Instant getFormattedLatestPriceTime()
    {
        return Instant.ofEpochSecond(latestPriceTime);
    }

    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }

    public Integer getCount()
    {
        return count;
    }
}
