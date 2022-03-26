package net.muon.data.sushiswap;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.dex.DexSource;
import org.apache.ignite.Ignite;

import java.util.List;
import java.util.Map;

public class SushiswapSource extends DexSource
{
    public SushiswapSource(Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges, Map<String, String> tokens, List<QuoteChangeListener> changeListeners)
    {
        super("sushiswap", ignite, mapper, endpoint, exchanges, tokens, changeListeners);
    }

    @Override
    public String getTokenAddressQuery(String symbol)
    {
        return String.format("{\n" +
                "  tokens(first: 1, where: {symbol: \"%s\"}, orderBy: volumeUSD, orderDirection: desc){\n" +
                "    id\n" +
                "  }\n" +
                "}", symbol);
    }
}
