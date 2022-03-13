package net.muon.data.nft;

import java.math.BigInteger;

public class TokenPrice
{
    private BigInteger price;
    private BigInteger cumulativePrice;
    private BigInteger count;

    public TokenPrice(BigInteger price, BigInteger cumulativePrice, BigInteger count)
    {
        this.price = price;
        this.cumulativePrice = cumulativePrice;
        this.count = count;
    }

    public TokenPrice CalculateNext(BigInteger price)
    {
        return new TokenPrice(price, this.cumulativePrice.add(price), this.count.add(BigInteger.ONE));
    }

    public BigInteger getPrice()
    {
        return price;
    }

    public void setPrice(BigInteger price)
    {
        this.price = price;
    }

    public BigInteger getCumulativePrice()
    {
        return cumulativePrice;
    }

    public void setCumulativePrice(BigInteger cumulativePrice)
    {
        this.cumulativePrice = cumulativePrice;
    }

    public BigInteger getCount()
    {
        return count;
    }

    public void setCount(BigInteger count)
    {
        this.count = count;
    }
}
