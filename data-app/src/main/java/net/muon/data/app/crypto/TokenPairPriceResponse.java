package net.muon.data.app.crypto;

import net.muon.data.core.TokenPair;

import java.math.BigDecimal;
import java.util.List;

public class TokenPairPriceResponse
{
    private TokenPair pair;
    private BigDecimal averagePrice;
    private List<ExchangePrice> prices;

    public TokenPair getPair()
    {
        return pair;
    }

    public void setPair(TokenPair pair)
    {
        this.pair = pair;
    }

    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice)
    {
        this.averagePrice = averagePrice;
    }

    public List<ExchangePrice> getPrices()
    {
        return prices;
    }

    public void setPrices(List<ExchangePrice> prices)
    {
        this.prices = prices;
    }
}
