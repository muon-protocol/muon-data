package net.muon.data.nft;

import java.math.BigDecimal;

public class AveragePrice
{
    private final BigDecimal average;
    private final Integer count;

    public AveragePrice(BigDecimal average, Integer count)
    {
        this.average = average;
        this.count = count;
    }

    public BigDecimal getAverage()
    {
        return average;
    }

    public Integer getCount()
    {
        return count;
    }
}
