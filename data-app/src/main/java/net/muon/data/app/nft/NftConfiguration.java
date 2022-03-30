package net.muon.data.app.nft;

import net.muon.data.core.SubgraphClient;
import net.muon.data.nft.OpenseaSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NftConfiguration
{
    @Bean
    @ConditionalOnProperty(name = "nft.opensea.enabled", havingValue = "true")
    public OpenseaSource openseaSource(SubgraphClient subgraphClient, NftProperties nftProperties)
    {
        return new OpenseaSource(subgraphClient, nftProperties.getOpensea().getSubgraphEndpoint());
    }
}
