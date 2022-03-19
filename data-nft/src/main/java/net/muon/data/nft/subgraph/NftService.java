package net.muon.data.nft.subgraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.SubgraphService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

public class NftService
{
    protected static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);

    private final ObjectMapper objectMapper;
    private final SubgraphService subgraphService;

    public NftService(String endpoint, ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
        subgraphService = new SubgraphService(endpoint, HttpClient.newBuilder().build(), objectMapper);
    }

    public Map<String, BigDecimal> getPrice(String collectionId, BigInteger nftId)
    {
        var priceData = fetchTokenPriceData(collectionId, nftId);
        if (priceData == null)
            return null;
        var sales = priceData.getData().getSales();
        if (sales.isEmpty())
            return null;
        var sum = sales.stream().map(SaleQueryResponse.SaleData::getPrice).reduce(BigInteger::add).get();
        var avg = new BigDecimal(sum).divide(BigDecimal.valueOf(sales.size()), PRECISION).divide(ETH_IN_WEI, PRECISION);

        var result = new HashMap<String, BigDecimal>();
        result.put("lastPrice", new BigDecimal(sales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION));
        result.put("averagePrice", avg);
        result.put("count", BigDecimal.valueOf(sales.size()));

        return result;
    }

    private SaleQueryResponse fetchTokenPriceData(String collection, BigInteger tokenId)
    {
        try {
            var response = subgraphService.fetchQueryResponse(getTokenQuery(collection, tokenId));
            return objectMapper.readValue(response, SaleQueryResponse.class);
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getTokenQuery(String collection, BigInteger tokenId)
    {
        return String.format("{\n" +
                "  sales(orderBy: timestamp, orderDirection: desc, where: {collection: \"%s\", tokenId: \"%s\", price_not: null}) {\n" +
                "    timestamp\n" +
                "    price\n" +
                "  }\n" +
                "}", collection, tokenId.toString());
    }
}
