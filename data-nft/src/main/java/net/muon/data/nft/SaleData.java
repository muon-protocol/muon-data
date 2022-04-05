package net.muon.data.nft;

import java.math.BigInteger;

class SaleData
{
    private String tokenId;
    private BigInteger price;
    private PaymentToken paymentToken;
    private BigInteger usdtPrice;
    private Long timestamp;

    public String getTokenId()
    {
        return tokenId;
    }

    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public BigInteger getPrice()
    {
        return price;
    }

    public void setPrice(BigInteger price)
    {
        this.price = price;
    }

    public PaymentToken getPaymentToken()
    {
        return paymentToken;
    }

    public void setPaymentToken(PaymentToken paymentToken)
    {
        this.paymentToken = paymentToken;
    }

    public BigInteger getUsdtPrice()
    {
        return usdtPrice;
    }

    public void setUsdtPrice(BigInteger usdtPrice)
    {
        this.usdtPrice = usdtPrice;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public static class PaymentToken
    {
        private String symbol;
        private BigInteger decimals;

        public String getSymbol()
        {
            return symbol;
        }

        public void setSymbol(String symbol)
        {
            this.symbol = symbol;
        }

        public BigInteger getDecimals()
        {
            return decimals;
        }

        public void setDecimals(BigInteger decimals)
        {
            this.decimals = decimals;
        }
    }
}
