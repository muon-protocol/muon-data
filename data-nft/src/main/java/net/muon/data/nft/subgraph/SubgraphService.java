package net.muon.data.nft.subgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class SubgraphService
{
    private final String endpoint;
    private final ObjectMapper objectMapper;
    protected static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);

    public SubgraphService(String endpoint, ObjectMapper objectMapper)
    {
        this.endpoint = endpoint;
        this.objectMapper = objectMapper;
    }

    public Map<String, BigDecimal> getPrice(String collectionId, BigInteger nftId)
    {
        var priceData = fetchTokenPriceData(collectionId, nftId);
        if (priceData == null)
            return null;
        var sales = priceData.getData().getSales();
        if (sales.isEmpty())
            return null;
        var sum = sales.stream().map(TokenPriceData.SaleData::getPrice).reduce(BigInteger::add).get();
        var avg = new BigDecimal(sum).divide(BigDecimal.valueOf(sales.size()), PRECISION).divide(ETH_IN_WEI, PRECISION);

        var result = new HashMap<String, BigDecimal>();
        result.put("lastPrice", new BigDecimal(sales.get(0).getPrice()).divide(ETH_IN_WEI, PRECISION));
        result.put("averagePrice", avg);
        result.put("count", BigDecimal.valueOf(sales.size()));

        return result;
    }

    private TokenPriceData fetchTokenPriceData(String collection, BigInteger tokenId)
    {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(getTokenQuery(collection, tokenId)))
                    .build();
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), TokenPriceData.class);
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getTokenQuery(String collection, BigInteger tokenId) throws JsonProcessingException
    {
        var query = new HashMap<String, String>();
        query.put("query", String.format("{\n" +
                "  sales(orderBy: timestamp, orderDirection: desc, where: {collection: \"%s\", tokenId: \"%s\", price_not: null}) {\n" +
                "    timestamp\n" +
                "    price\n" +
                "  }\n" +
                "}", collection, tokenId.toString()));
        return objectMapper.writeValueAsString(query);
    }
}
