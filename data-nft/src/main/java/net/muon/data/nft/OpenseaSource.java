package net.muon.data.nft;

import com.google.common.base.Strings;
import net.muon.data.core.BigDecimals;
import net.muon.data.core.SubgraphClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OpenseaSource
{
    private static final int PAGE_SIZE = 500;

    private final URI endpoint;
    private final SubgraphClient subgraphClient;

    public OpenseaSource(SubgraphClient subgraphClient, String endpoint)
    {
        this.endpoint = URI.create(endpoint);
        this.subgraphClient = subgraphClient;
    }

    public List<NftSale> getSales(String collectionId, BigInteger tokenId,
                                  Long fromTimestamp, Long toTimestamp,
                                  BigDecimal fromPrice, BigDecimal toPrice,
                                  String order, Boolean desc,
                                  Integer limit, Integer offset)
    {
        List<OpenseaSale> sales = fetchSales(collectionId, tokenId, fromTimestamp, toTimestamp, fromPrice, toPrice,
                prepareOrder(order), desc, limit, offset);
        return sales.stream().map(NftSale::new).collect(Collectors.toList());
    }

    public NftSale getLastSale(String collectionId, BigInteger tokenId,
                               Long fromTimestamp, Long toTimestamp,
                               BigDecimal fromPrice, BigDecimal toPrice)
    {
        return getSales(collectionId, tokenId, fromTimestamp, toTimestamp, fromPrice, toPrice, "time", true, 1, 0)
                .stream().findFirst().orElse(null);
    }

    public AveragePrice getAveragePrice(String collectionId, BigInteger tokenId,
                                        Long fromTimestamp, Long toTimestamp,
                                        BigDecimal fromPrice, BigDecimal toPrice)
    {
        if (fromTimestamp == null || toTimestamp == null)
            throw new IllegalArgumentException("time period must be provided");

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        List<NftSale> sales;
        do {
            sales = getSales(collectionId, tokenId, fromTimestamp, toTimestamp, fromPrice, toPrice, null, null, PAGE_SIZE, count);
            sum = sales.stream().map(NftSale::getPrice).reduce(sum, BigDecimal::add);
            count += sales.size();
        } while (sales.size() == PAGE_SIZE && count < 5000);

        if (count == 0)
            return null;

        return new AveragePrice(BigDecimals.divide(sum, count), count);
    }

    public NftSale getFloorPrice(String collectionId, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        return getSales(collectionId, tokenId, fromTimestamp, toTimestamp, null, null, "price", false, 1, 0)
                .stream().findFirst().orElse(null);
    }

    private List<OpenseaSale> fetchSales(String collection, BigInteger tokenId,
                                         Long fromTimestamp, Long toTimestamp,
                                         BigDecimal fromPrice, BigDecimal toPrice,
                                         String order, Boolean desc, Integer limit, Integer offset)
    {
        List<String> filters = new ArrayList<>();
        if (!Strings.isNullOrEmpty(collection))
            filters.add(String.format("collection: \"%s\"", collection.toLowerCase()));
        if (tokenId != null)
            filters.add(String.format("tokenId: \"%s\"", tokenId));
        filters.addAll(getTimestampFilters(fromTimestamp, toTimestamp));
        filters.addAll(getPriceFilters(fromPrice, toPrice));

        List<String> criteria = new ArrayList<>();
        if (offset != null)
            criteria.add("skip: " + offset);
        criteria.add("first: " + (limit == null ? PAGE_SIZE : limit));
        if (order != null)
            criteria.add("orderBy: " + order);
        if (desc != null)
            criteria.add("orderDirection: " + (desc ? "desc" : "asc"));
        if (!filters.isEmpty())
            criteria.add(String.format("where: {%s}", String.join(", ", filters)));

        String query = String.format("{\n" +
                "   sales (%s) {\n" +
                "       collection\n" +
                "       tokenId\n" +
                "       timestamp\n" +
                "       price\n" +
                "       paymentToken {\n" +
                "           symbol\n" +
                "           decimals\n" +
                "       }\n" +
                "       usdtPrice\n" +
                "   }\n" +
                "}", String.join(", ", criteria));

        return subgraphClient.send(endpoint, query, SalesData.class).getSales();
    }

    private static List<String> getTimestampFilters(Long from, Long to)
    {
        if (from != null && to != null && from >= to)
            throw new IllegalArgumentException("Invalid time period");

        List<String> filters = new ArrayList<>();
        if (from != null)
            filters.add("timestamp_gte: " + from);
        if (to != null)
            filters.add("timestamp_lt: " + to);
        return filters;
    }

    private static List<String> getPriceFilters(BigDecimal from, BigDecimal to)
    {
        if (from != null && to != null && from.compareTo(to) >= 0)
            throw new IllegalArgumentException("Invalid price range");

        List<String> filters = new ArrayList<>();
        if (from != null)
            filters.add("usdtPrice_gte: " + BigDecimals.multiplyByScale(from, 6).longValue());
        if (to != null)
            filters.add("usdtPrice_lt: " + BigDecimals.multiplyByScale(to, 6).longValue());
        return filters;
    }

    private static String prepareOrder(String order)
    {
        if (order == null)
            return null;
        switch (order) {
            case "time":
                return "timestamp";
            case "price":
                return "usdtPrice";
            default:
                throw new IllegalArgumentException(order);
        }
    }

    private static class SalesData
    {
        private List<OpenseaSale> sales;

        public List<OpenseaSale> getSales()
        {
            return sales;
        }

        public void setSales(List<OpenseaSale> sales)
        {
            this.sales = sales;
        }
    }
}
