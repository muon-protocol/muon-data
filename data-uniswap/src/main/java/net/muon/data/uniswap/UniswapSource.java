package net.muon.data.uniswap;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.dex.DexSource;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.Map;

public class UniswapSource extends DexSource
{
    public UniswapSource(Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges, Map<String, String> tokens, List<QuoteChangeListener> changeListeners)
    {
        super("uniswap", ignite, mapper, endpoint, exchanges, tokens, changeListeners);
    }

    @Override
    protected String getTokenAddressQuery(String symbol)
    {
        return String.format("{\n" +
                "  tokens(first: 1, where: {symbol: \"%s\"}, orderBy: tradeVolumeUSD, orderDirection: desc){\n" +
                "    id\n" +
                "  }\n" +
                "}", symbol);
    }
}
