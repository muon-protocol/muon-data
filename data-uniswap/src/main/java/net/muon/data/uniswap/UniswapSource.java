package net.muon.data.uniswap;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.dex.DexSource;
import org.apache.ignite.Ignite;

import java.util.List;

public class UniswapSource extends DexSource
{
    public UniswapSource(Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges, List<String> symbols, List<QuoteChangeListener> changeListeners)
    {
        super("uniswap", ignite, mapper, endpoint, exchanges, symbols, changeListeners);
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
