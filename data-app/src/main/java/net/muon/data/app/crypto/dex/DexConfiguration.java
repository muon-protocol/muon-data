package net.muon.data.app.crypto.dex;

import net.muon.data.core.SubgraphClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DexConfiguration
{
    @Bean
    @ConditionalOnProperty(name = "uniswap.enabled", havingValue = "true")
    public UniswapV2Source uniswapV2Source(SubgraphClient subgraphClient, UniswapV2Properties properties)
    {
        return new UniswapV2Source(subgraphClient, properties);
    }

    @Bean
    @ConditionalOnProperty(name = "sushiswap.enabled", havingValue = "true")
    public SushiswapSource sushiswapSource(SubgraphClient subgraphClient, SushiswapProperties properties)
    {
        return new SushiswapSource(subgraphClient, properties);
    }
}
