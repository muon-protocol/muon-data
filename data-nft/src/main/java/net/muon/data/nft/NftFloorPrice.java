package net.muon.data.nft;

import net.muon.data.core.BigDecimals;

import java.math.BigDecimal;
import java.time.Instant;

public class NftFloorPrice
{
    private final String tokenId;
    private final BigDecimal price;
    private final String paymentToken;
    private final BigDecimal usdtPrice;
    private final Long time;

    public NftFloorPrice(SaleData floorSale)
    {
        this.tokenId = floorSale.getTokenId();
        var paymentToken = floorSale.getPaymentToken();
        this.price = BigDecimals.divideByScale(floorSale.getPrice(), paymentToken.getDecimals());
        this.paymentToken = paymentToken.getSymbol();
        this.usdtPrice = BigDecimals.divideByScale(floorSale.getUsdtPrice(), 6);
        this.time = floorSale.getTimestamp();
    }

    public String getTokenId()
    {
        return tokenId;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getPaymentToken()
    {
        return paymentToken;
    }

    public BigDecimal getUsdtPrice()
    {
        return usdtPrice;
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
