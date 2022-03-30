package net.muon.data.app.crypto;

import net.muon.data.core.SubgraphClient;
import net.muon.data.core.DexSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

public class UniswapSource extends DexSource
{
    public UniswapSource(SubgraphClient subgraphClient, UniswapProperties properties)
    {
        super(subgraphClient, properties.subgraphEndpoint, properties.getTokens());
    }

    @Component
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "uniswap")
    public static class UniswapProperties extends CryptoConfiguration.DexProperties
    {
        @Value("${uniswap.subgraph-endpoint}")
        private String subgraphEndpoint;
    }
}
