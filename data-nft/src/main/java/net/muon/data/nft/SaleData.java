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

    private static class PaymentToken
    {
        private String name;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
