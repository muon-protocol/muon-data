package net.muon.data.nft.subgraph;

import net.muon.data.core.SubgraphClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NftService
{
    private static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);
    private static final int PAGE_SIZE = 1000;

    private final URI endpoint;
    private final SubgraphClient subgraphClient;

    public NftService(SubgraphClient subgraphClient, String endpoint)
    {
        this.endpoint = URI.create(endpoint);
        this.subgraphClient = subgraphClient;
    }

    public Map<String, BigDecimal> getPrice(String collectionId, BigInteger nftId, Long fromTimestamp, Long toTimestamp)
    {
        checkTimePeriod(fromTimestamp, toTimestamp);
        var allSales = new ArrayList<SaleData>();
        List<SaleData> sales;
        do {
            var tokenData = fetchTokenPriceData(collectionId, nftId, fromTimestamp, toTimestamp, allSales.size());
            if (tokenData == null || tokenData.getToken() == null)
                return null;
            sales = tokenData.getToken().getSales();
            allSales.addAll(sales);
        } while (sales.size() == PAGE_SIZE);

        if (allSales.isEmpty())
            return null;

        var sum = allSales.stream().map(SaleData::getPrice).reduce(BigInteger::add).get();
        var avg = new BigDecimal(sum).divide(BigDecimal.valueOf(allSales.size()), PRECISION).divide(ETH_IN_WEI, PRECISION);

        var result = new HashMap<String, BigDecimal>();
        result.put("lastPrice", new BigDecimal(allSales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION));
        result.put("averagePrice", avg);
        result.put("count", BigDecimal.valueOf(allSales.size()));

        return result;
    }

    public BigDecimal getFloorPrice(String collectionId, BigInteger nftId, Long fromTimestamp, Long toTimestamp)
    {
        checkTimePeriod(fromTimestamp, toTimestamp);
        var priceData = fetchFloorPrice(collectionId, nftId, fromTimestamp, toTimestamp);
        if (priceData == null)
            return null;
        var sales = priceData.getSales();
        if (sales.isEmpty())
            return null;
        return new BigDecimal(sales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION);
    }

    private TokenData fetchTokenPriceData(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp, long skip)
    {
        var timeFilter = fromTimestamp == null || toTimestamp == null ? "" :
                String.format(", where: {timestamp_gte: %d, timestamp_lte: %d}", fromTimestamp, toTimestamp);

        String query = String.format("{\n" +
                "   token(id: \"%s:%s\") {\n" +
                "       sales (skip: %d, first: %d , orderBy: timestamp, orderDirection: desc%s) {" +
                "           price\n" +
                "       }\n" +
                "   }\n" +
                "}", collection, tokenId, skip, PAGE_SIZE, timeFilter);

        return subgraphClient.send(endpoint, query, TokenData.class);
    }

    private SalesData fetchFloorPrice(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        var timeFilter = fromTimestamp == null || toTimestamp == null ? "" :
                String.format(", timestamp_gte: %d, timestamp_lte: %d", fromTimestamp, toTimestamp);

        var tokenFilter = tokenId == null ?
                String.format("token_starts_with: \"%s:\"", collection.toLowerCase()) :
                String.format("token: \"%s:%s\"", collection.toLowerCase(), tokenId);

        String query = String.format("{\n" +
                "   sales(first: 1, orderBy: price, orderDirection: asc, where : {%s%s}){\n" +
                "       price\n" +
                "   }\n" +
                "}", tokenFilter, timeFilter);

        return subgraphClient.send(endpoint, query, SalesData.class);
    }

    private void checkTimePeriod(Long fromTimestamp, Long toTimestamp)
    {
        if (fromTimestamp == null && toTimestamp == null) return;
        if (fromTimestamp == null || toTimestamp == null || fromTimestamp > toTimestamp)
            throw new IllegalArgumentException("Invalid time period");
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

    private static class TokenData
    {
        private SalesData token;

        public SalesData getToken()
        {
            return token;
        }

        public void setToken(SalesData token)
        {
            this.token = token;
        }
    }
}
