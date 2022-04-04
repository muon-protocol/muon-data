package net.muon.data.core;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class DexSource extends AbstractHttpSource
{
    private final URI endpoint;
    private final SubgraphClient subgraphClient;
    private final Map<String, String> tokenAddresses;

    public DexSource(String id, SubgraphClient subgraphClient, String endpoint, Map<String, String> tokenAddresses)
    {
        super(id);
        this.subgraphClient = subgraphClient;
        this.endpoint = URI.create(endpoint);
        this.tokenAddresses = tokenAddresses;
    }

    @Override
    protected TokenPairPrice load(TokenPair pair)
    {
        var token0Address = tokenAddresses.get(pair.token0());
        if (token0Address == null)
            return null;

        var token1Address = tokenAddresses.get(pair.token1());
        if (token1Address == null)
            return null;

        var token0PriceRequested = token0Address.compareTo(token1Address) < 0;
        var token0 = token0PriceRequested ? token0Address : token1Address;
        var token1 = token0PriceRequested ? token1Address : token0Address;

        BigDecimal price = fetchTokenPrice(token0, token1, token0PriceRequested);
        if (price == null)
            return null;

        return new TokenPairPrice(pair, price, Instant.now());
    }

    private BigDecimal fetchTokenPrice(String token0, String token1, boolean token0PriceRequested)
    {
        String query = String.format("{\n" +
                "  pairs(first: 1, where: {token0: \"%s\", token1: \"%s\"}){\n" +
                "    reserve0\n" +
                "    reserve1\n" +
                "  }\n" +
                "}", token0, token1);

        var result = subgraphClient.send(endpoint, query, Result.class);
        List<PairData> pairs = result.pairs;
        if (pairs == null || pairs.isEmpty())
            return null;

        PairData pair = pairs.get(0);
        if (token0PriceRequested)
            return BigDecimals.divide(pair.getReserve1(), pair.getReserve0());

        return BigDecimals.divide(pair.getReserve0(), pair.getReserve1());
    }

    private static class Result
    {
        List<PairData> pairs;

        List<PairData> getPairs()
        {
            return pairs;
        }

        void setPairs(List<PairData> pairs)
        {
            this.pairs = pairs;
        }
    }

    private static class PairData
    {
        BigDecimal reserve0;
        BigDecimal reserve1;

        BigDecimal getReserve0()
        {
            return reserve0;
        }

        void setReserve0(BigDecimal reserve0)
        {
            this.reserve0 = reserve0;
        }

        BigDecimal getReserve1()
        {
            return reserve1;
        }

        void setReserve1(BigDecimal reserve1)
        {
            this.reserve1 = reserve1;
        }
    }
}
