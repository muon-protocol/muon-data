package net.muon.data.app;

import net.muon.data.nft.NftService;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.Set;

@Configuration
public class NftConfiguration
{
    @Bean
    public NftService nftService(@Value("${web3.provider}") String web3Provider,
                                 @Value("${nft.starting-block-number}") BigInteger startingBlockNumber,
                                 @Value("${collections}") Set<String> collections, Ignite ignite)
    {
        return new NftService(web3Provider, startingBlockNumber, collections, ignite);
    }
}
