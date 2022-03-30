package net.muon.data.app.crypto;

import net.muon.data.core.SubgraphClient;
import net.muon.data.core.incubator.DexSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

public class SushiswapSource extends DexSource
{
    public SushiswapSource(SubgraphClient subgraphClient, SushiswapProperties properties)
    {
        super(subgraphClient, properties.subgraphEndpoint, properties.getTokens());
    }

    @Component
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "sushiswap")
    public static class SushiswapProperties extends CryptoConfiguration.DexProperties
    {
        @Value("${sushiswap.subgraph-endpoint}")
        private String subgraphEndpoint;
    }
}
