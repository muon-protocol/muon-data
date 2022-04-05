package net.muon.data.nft;

import net.muon.data.core.BigDecimals;

import java.math.BigDecimal;
import java.time.Instant;

public class NftPrice
{
    private final BigDecimal lastSalePrice;
    private final String lastSaleToken;
    private final BigDecimal lastSaleUsdtPrice;
    private final Long lastSaleTime;
    private final Integer totalSales;

    public NftPrice(SaleData lastSale, Integer totalSales)
    {
        var paymentToken = lastSale.getPaymentToken();
        this.lastSalePrice = BigDecimals.divideByScale(lastSale.getPrice(), paymentToken.getDecimals());
        this.lastSaleToken = paymentToken.getSymbol();
        this.lastSaleUsdtPrice = BigDecimals.divideByScale(lastSale.getUsdtPrice(), 6);
        this.lastSaleTime = lastSale.getTimestamp();
        this.totalSales = totalSales;
    }

    public BigDecimal getLastSalePrice()
    {
        return lastSalePrice;
    }

    public String getLastSaleToken()
    {
        return lastSaleToken;
    }

    public BigDecimal getLastSaleUsdtPrice()
    {
        return lastSaleUsdtPrice;
    }

    public Long getLastSaleTime()
    {
        return lastSaleTime;
    }

    public Instant getFormattedLastSaleTime()
    {
        return Instant.ofEpochSecond(lastSaleTime);
    }

    public Integer getTotalSales()
    {
        return totalSales;
    }
}
