package net.muon.data.nft;

import com.google.common.base.Strings;
import net.muon.data.core.SubgraphClient;

import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OpenseaSource
{
    private static final String ETH_ID = "0x0000000000000000000000000000000000000000";
    private static final String WETH_ID = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";
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
        long now = Instant.now().toEpochMilli() / 1000;
        if (toTimestamp == null || toTimestamp > now)
            toTimestamp = now;

        SaleData latestPrice = null;
        BigInteger sum = BigInteger.ZERO;
        int count = 0;
        List<SaleData> sales;
        do {
            var salesData = fetchTokenPrice(collectionId, nftId, fromTimestamp, toTimestamp, count);
            if (salesData == null || salesData.getSales() == null)
                return null;
            sales = salesData.getSales();
            sum = sales.stream().map(SaleData::getPrice).reduce(sum, BigInteger::add);
            if (count == 0 && !sales.isEmpty())
                latestPrice = sales.get(0);
            count += sales.size();
        } while (sales.size() == PAGE_SIZE);

        if (count == 0)
            return null;

        return new NftPrice(latestPrice, count);
    }

    public NftFloorPrice getFloorPrice(String collectionId, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        var priceData = fetchFloorPrice(collectionId, tokenId, fromTimestamp, toTimestamp);

        if (priceData == null)
            return null;
        var sales = priceData.getSales();
        if (sales.isEmpty())
            return null;

        return new NftFloorPrice(sales.get(0));
    }

    private SalesData fetchTokenPrice(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp, int skip)
    {
        checkArgument(!Strings.isNullOrEmpty(collection));
        checkNotNull(tokenId);
        return fetchSales(collection, tokenId, null, fromTimestamp, toTimestamp, "timestamp", true, PAGE_SIZE, skip);
    }

    private SalesData fetchFloorPrice(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        checkArgument(!Strings.isNullOrEmpty(collection));
        return fetchSales(collection, tokenId, List.of(ETH_ID, WETH_ID), fromTimestamp, toTimestamp, "price", false, 1, 0);
    }

    private SalesData fetchSales(String collection, BigInteger tokenId,
                                 List<String> paymentTokenIds,
                                 Long fromTimestamp, Long toTimestamp,
                                 String order, Boolean desc, Integer limit, Integer offset)
    {
        List<String> filters = new ArrayList<>();
        if (!Strings.isNullOrEmpty(collection))
            filters.add(String.format("collection: \"%s\"", collection.toLowerCase()));
        if (tokenId != null)
            filters.add(String.format("tokenId: \"%s\"", tokenId));
        if (paymentTokenIds != null && !paymentTokenIds.isEmpty()) {
            String tokens = paymentTokenIds.stream().map(id -> String.format("\"%s\"", id)).collect(Collectors.joining(","));
            filters.add(String.format("paymentToken_in: [%s]", tokens));
        }
        filters.addAll(getTimestampFilters(fromTimestamp, toTimestamp));

        List<String> criteria = new ArrayList<>();
        if (offset != null)
            criteria.add("skip: " + offset);
        if (limit != null)
            criteria.add("first: " + limit);
        if (order != null)
            criteria.add("orderBy: " + order);
        if (desc != null)
            criteria.add("orderDirection: " + (desc ? "desc" : "asc"));
        if (!filters.isEmpty())
            criteria.add(String.format("where: {%s}", String.join(", ", filters)));

        String query = String.format("{\n" +
                "   sales (%s) {\n" +
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

        return subgraphClient.send(endpoint, query, SalesData.class);
    }

    private static List<String> getTimestampFilters(Long fromTimestamp, Long toTimestamp)
    {
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp >= toTimestamp)
            throw new IllegalArgumentException("Invalid time period");

        List<String> filters = new ArrayList<>();
        if (fromTimestamp != null)
            filters.add("timestamp_gte: " + fromTimestamp);
        if (toTimestamp != null)
            filters.add("timestamp_lt: " + toTimestamp);
        return filters;
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
}
