package net.muon.data.nft;

import net.muon.data.core.SubgraphClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class OpenseaSource
{
    private static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);
    private static final int PAGE_SIZE = 1000;

    private final URI endpoint;
    private final SubgraphClient subgraphClient;

    public OpenseaSource(SubgraphClient subgraphClient, String endpoint)
    {
        this.endpoint = URI.create(endpoint);
        this.subgraphClient = subgraphClient;
    }

    public NftPrice getPrice(String collectionId, BigInteger nftId, Long fromTimestamp, Long toTimestamp)
    {
        toTimestamp = checkTimePeriod(fromTimestamp, toTimestamp);

        SaleData latestPrice = null;
        BigInteger sum = BigInteger.ZERO;
        int count = 0;
        List<SaleData> sales;
        do {
            var tokenData = fetchTokenPriceData(collectionId, nftId, fromTimestamp, toTimestamp, count);
            if (tokenData == null || tokenData.getToken() == null)
                return null;
            sales = tokenData.getToken().getSales();
            sum = sales.stream().map(SaleData::getPrice).reduce(sum, BigInteger::add);
            if (count == 0 && !sales.isEmpty())
                latestPrice = sales.get(0);
            count += sales.size();
        } while (sales.size() == PAGE_SIZE);

        if (count == 0)
            return null;

        var avg = new BigDecimal(sum).divide(BigDecimal.valueOf(count), PRECISION).divide(ETH_IN_WEI, PRECISION);
        BigDecimal lastPrice = new BigDecimal(latestPrice.getPrice()).divide(ETH_IN_WEI, PRECISION);
        return new NftPrice(lastPrice, latestPrice.getTimestamp(), avg, count);
    }

    public NftFloorPrice getFloorPrice(String collectionId, BigInteger nftId, Long fromTimestamp, Long toTimestamp)
    {
        toTimestamp = checkTimePeriod(fromTimestamp, toTimestamp);
        var priceData = fetchFloorPrice(collectionId, nftId, fromTimestamp, toTimestamp);

        if (priceData == null)
            return null;
        var sales = priceData.getSales();
        if (sales.isEmpty())
            return null;

        SaleData sale = sales.get(0);
        BigDecimal price = new BigDecimal(sale.getPrice()).divide(ETH_IN_WEI, PRECISION);
        return new NftFloorPrice(price, sale.getTimestamp());
    }

    private TokenData fetchTokenPriceData(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp, long skip)
    {
        var fromTimeFilter = fromTimestamp != null ? String.format("timestamp_gte: %d, ", fromTimestamp) : "";
        var timeFilter = fromTimeFilter + String.format("timestamp_lte: %d", toTimestamp);

        String query = String.format("{\n" +
                "   token(id: \"%s:%s\") {\n" +
                "       sales (skip: %d, first: %d , orderBy: timestamp, orderDirection: desc, where: {%s}) {" +
                "           price\n" +
                "           timestamp\n" +
                "       }\n" +
                "   }\n" +
                "}", collection, tokenId, skip, PAGE_SIZE, timeFilter);

        return subgraphClient.send(endpoint, query, TokenData.class);
    }

    private SalesData fetchFloorPrice(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        var fromTimeFilter = fromTimestamp != null ? String.format("timestamp_gte: %d, ", fromTimestamp) : "";
        var timeFilter = fromTimeFilter + String.format("timestamp_lte: %d", toTimestamp);

        var tokenFilter = tokenId == null ?
                String.format("token_starts_with: \"%s:\"", collection.toLowerCase()) :
                String.format("token: \"%s:%s\"", collection.toLowerCase(), tokenId);

        String query = String.format("{\n" +
                "   sales(first: 1, orderBy: price, orderDirection: asc, where : {%s, %s}){\n" +
                "       price\n" +
                "       timestamp\n" +
                "   }\n" +
                "}", tokenFilter, timeFilter);

        return subgraphClient.send(endpoint, query, SalesData.class);
    }

    private Long checkTimePeriod(Long fromTimestamp, Long toTimestamp)
    {
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp > toTimestamp)
            throw new IllegalArgumentException("Invalid time period");
        long now = Instant.now().toEpochMilli() / 1000;
        return toTimestamp == null || toTimestamp > now ? now : toTimestamp;
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
        private BigInteger price;
        private Long timestamp;

        public Long getTimestamp()
        {
            return timestamp;
        }

        public void setTimestamp(Long timestamp)
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

    public static void main(String[] args)
    {
        System.out.println(Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli());
        ;
    }
}
