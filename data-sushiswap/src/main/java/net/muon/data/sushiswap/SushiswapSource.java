package net.muon.data.sushiswap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.core.QuoteChangeListener;
import net.muon.data.core.dex.DexSource;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.Collections;
import java.util.List;

public class SushiswapSource extends DexSource
{
    public SushiswapSource(Ignite ignite, ObjectMapper mapper, String endpoint, List<String> exchanges, List<String> symbols, List<QuoteChangeListener> changeListeners)
    {
        super("sushiswap", ignite, mapper, endpoint, exchanges, symbols, changeListeners);
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
