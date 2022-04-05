package net.muon.data.nft;

import net.muon.data.core.BigDecimals;

import java.math.BigDecimal;
import java.time.Instant;

public class NftSale
{
    private final String collection;
    private final String tokenId;
    private final BigDecimal price;
    private final Long time;

    public NftSale(OpenseaSale sale)
    {
        this.collection = sale.getCollection();
        this.tokenId = sale.getTokenId();
        this.price = BigDecimals.divideByScale(sale.getUsdtPrice(), 6);
        this.time = sale.getTimestamp();
    }

    public String getCollection()
    {
        return collection;
    }

    public String getTokenId()
    {
        return tokenId;
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
