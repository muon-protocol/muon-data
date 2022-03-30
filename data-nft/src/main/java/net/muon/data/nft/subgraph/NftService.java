package net.muon.data.nft.subgraph;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.SubgraphService;

import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NftService
{
    protected static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);
    private static final int PAGE_SIZE = 1000;

    private final ObjectMapper objectMapper;
    private final SubgraphService subgraphService;

    public NftService(String endpoint, ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
        subgraphService = new SubgraphService(endpoint, HttpClient.newBuilder().build(), objectMapper);
    }

    public Map<String, BigDecimal> getPrice(String collectionId, BigInteger nftId, Long fromTimestamp, Long toTimestamp)
    {
        toTimestamp = checkTimePeriod(fromTimestamp, toTimestamp);
        var allSales = new ArrayList<SalesData.SaleData>();
        List<SalesData.SaleData> sales;
        do {
            var priceData = fetchTokenPriceData(collectionId, nftId, fromTimestamp, toTimestamp, allSales.size());
            if (priceData == null || priceData.getData().getToken() == null)
                return null;
            sales = priceData.getData().getToken().getSales();
            allSales.addAll(sales);
        } while (sales.size() == PAGE_SIZE);

        if (allSales.isEmpty())
            return null;

        var sum = allSales.stream().map(SalesData.SaleData::getPrice).reduce(BigInteger::add).get();
        var avg = new BigDecimal(sum).divide(BigDecimal.valueOf(allSales.size()), PRECISION).divide(ETH_IN_WEI, PRECISION);

        var result = new HashMap<String, BigDecimal>();
        result.put("lastPrice", new BigDecimal(allSales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION));
        result.put("averagePrice", avg);
        result.put("count", BigDecimal.valueOf(allSales.size()));

        return result;
    }

    public BigDecimal getFloorPrice(String collectionId, BigInteger nftId, Long fromTimestamp, Long toTimestamp)
    {
        toTimestamp = checkTimePeriod(fromTimestamp, toTimestamp);
        var priceData = fetchFloorPrice(collectionId, nftId, fromTimestamp, toTimestamp);
        if (priceData == null)
            return null;
        var sales = priceData.getData().getSales();
        if (sales.isEmpty())
            return null;
        return new BigDecimal(sales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION);
    }

    private QueryResponse<TokenData> fetchTokenPriceData(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp, long skip)
    {
        try {
            var response = subgraphService.fetchQueryResponse(getTokenPricesQuery(collection, tokenId, fromTimestamp, toTimestamp, skip));
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private QueryResponse<SalesData> fetchFloorPrice(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        try {
            var response = subgraphService.fetchQueryResponse(getFloorPriceQuery(collection, tokenId, fromTimestamp, toTimestamp));
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getTokenPricesQuery(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp, long skip)
    {
        var fromTimeFilter = fromTimestamp != null ? String.format("timestamp_gte: %d, ", fromTimestamp) : "";
        var timeFilter = fromTimeFilter + String.format("timestamp_lte: %d", toTimestamp);

        return String.format("{\n" +
                "   token(id: \"%s:%s\") {\n" +
                "       sales (skip: %d, first: %d , orderBy: timestamp, orderDirection: desc, where: {%s}) {" +
                "           price\n" +
                "       }\n" +
                "   }\n" +
                "}", collection, tokenId, skip, PAGE_SIZE, timeFilter);
    }

    private String getFloorPriceQuery(String collection, BigInteger tokenId, Long fromTimestamp, Long toTimestamp)
    {
        var fromTimeFilter = fromTimestamp != null ? String.format("timestamp_gte: %d, ", fromTimestamp) : "";
        var timeFilter = fromTimeFilter + String.format("timestamp_lte: %d", toTimestamp);

        var tokenFilter = tokenId == null ?
                String.format("token_starts_with: \"%s:\"", collection.toLowerCase()) :
                String.format("token: \"%s:%s\"", collection.toLowerCase(), tokenId);

        return String.format("{\n" +
                "   sales(first: 1, orderBy: price, orderDirection: asc, where : {%s, %s}){\n" +
                "       price\n" +
                "   }\n" +
                "}", tokenFilter, timeFilter);
    }

    private Long checkTimePeriod(Long fromTimestamp, Long toTimestamp)
    {
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp > toTimestamp)
            throw new BadRequestException("Invalid time period"); // FIXME ?
        long now = Instant.now().toEpochMilli() / 1000;
        return toTimestamp == null || toTimestamp > now ? now : toTimestamp;
    }
}
