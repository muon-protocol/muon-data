package net.muon.data.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.muon.data.nft.subgraph.NftService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NftConfiguration
{
//    @Bean
//    public NftService nftService(@Value("${web3.provider}") String web3Provider,
//                                 @Value("${nft.starting-block-number}") BigInteger startingBlockNumber,
//                                 @Value("${nft.collections}") Set<String> collections, Ignite ignite)
//    {
//        return new NftService(web3Provider, startingBlockNumber, collections, ignite);
//    }

    @Bean
    public NftService nftService(@Value("${nft.subgraph-endpoint}") String endpoint, ObjectMapper objectMapper)
    {
        return new NftService(endpoint, objectMapper);
    }
}
