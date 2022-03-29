package net.muon.data.nft.subgraph;

import net.muon.data.core.SubgraphClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NftService
{
    protected static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);

    private final URI endpoint;
    private final SubgraphClient subgraphClient;

    public NftService(SubgraphClient subgraphClient, String endpoint)
    {
        this.endpoint = URI.create(endpoint);
        this.subgraphClient = subgraphClient;
    }

    public Map<String, BigDecimal> getPrice(String collectionId, BigInteger nftId)
    {
        var priceData = fetchTokenPriceData(collectionId, nftId);
        if (priceData == null)
            return null;
        var sales = priceData.getSales();
        if (sales.isEmpty())
            return null;
        var sum = sales.stream().map(SaleData::getPrice).reduce(BigInteger::add).get();
        var avg = new BigDecimal(sum).divide(BigDecimal.valueOf(sales.size()), PRECISION).divide(ETH_IN_WEI, PRECISION);

        var result = new HashMap<String, BigDecimal>();
        result.put("lastPrice", new BigDecimal(sales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION));
        result.put("averagePrice", avg);
        result.put("count", BigDecimal.valueOf(sales.size()));

        return result;
    }

    private SalesData fetchTokenPriceData(String collection, BigInteger tokenId)
    {
        String query = String.format("{\n" +
                "  sales(orderBy: timestamp, orderDirection: desc, where: {collection: \"%s\", tokenId: \"%s\", price_not: null}) {\n" +
                "    timestamp\n" +
                "    price\n" +
                "  }\n" +
                "}", collection, tokenId.toString());
        return subgraphClient.send(endpoint, query, SalesData.class);
    }

    private static class SalesData
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
    }

    private static class SaleData
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
