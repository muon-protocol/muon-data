package net.muon.data.app.crypto.dex;

import net.muon.data.app.crypto.Exchange;
import net.muon.data.core.DexSource;
import net.muon.data.core.SubgraphClient;

public class UniswapV2Source extends DexSource
{
    public UniswapV2Source(SubgraphClient subgraphClient, UniswapV2Properties properties)
    {
        super(Exchange.UNISWAP_V2.getId(), subgraphClient, properties.getSubgraphEndpoint(), properties.getTokenAddresses());
    }
}
