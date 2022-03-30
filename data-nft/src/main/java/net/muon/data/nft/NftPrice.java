package net.muon.data.nft;

import java.math.BigDecimal;

public class NftPrice
{
    private final BigDecimal lastPrice;
    private final BigDecimal averagePrice;
    private final Integer count;

    public NftPrice(BigDecimal lastPrice, BigDecimal averagePrice, Integer count)
    {
        this.lastPrice = lastPrice;
        this.averagePrice = averagePrice;
        this.count = count;
    }

    public BigDecimal getLastPrice()
    {
        return lastPrice;
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
