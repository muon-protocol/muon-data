package net.muon.data.nft.subgraph;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

class SalesData
{
    private List<SaleData> sales;

    public List<SaleData> getSales()
    {
        return sales;
    }

    public void setSales(List<SaleData> sales)
    {
        this.sales = sales;
    }

    public static class SaleData
    {
        private Timestamp timestamp; // FIXME
        private BigInteger price;

        public Timestamp getTimestamp()
        {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp)
        {
            this.timestamp = timestamp;
        }

        public BigInteger getPrice()
        {
            return price;
        }

        public void setPrice(BigInteger price)
        {
            this.price = price;
        }
    }
}
