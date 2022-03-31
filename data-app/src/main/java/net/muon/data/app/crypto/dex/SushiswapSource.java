package net.muon.data.app.crypto.dex;

import net.muon.data.app.crypto.Exchange;
import net.muon.data.core.DexSource;
import net.muon.data.core.SubgraphClient;

public class SushiswapSource extends DexSource
{
    public SushiswapSource(SubgraphClient subgraphClient, SushiswapProperties properties)
    {
        super(Exchange.SUSHISWAP.getId(), subgraphClient, properties.getSubgraphEndpoint(), properties.getTokenAddresses());
    }
}
