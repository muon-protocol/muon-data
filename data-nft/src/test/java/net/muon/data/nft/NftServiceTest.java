package net.muon.data.nft;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public class NftServiceTest
{
    private static final Ignite ignite = getIgnite();
    private static final String PROVIDER = "https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161";
    private static final String STORAGE_PATH = "/tmp"; // FIXME
    public static final String COLLECTION = "0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d";

    @Test
    public void test() throws IOException
    {
        NftService nftService = getNftService(COLLECTION);

        var start = BigInteger.valueOf(14375984);
        var end = BigInteger.valueOf(14376268);
        nftService.handleSaleLogs(start, end);

        Assertions.assertEquals(nftService.getPrice(COLLECTION, BigInteger.valueOf(2577)), BigDecimal.valueOf(68.004));
        Assertions.assertEquals(nftService.getPrice(COLLECTION, BigInteger.valueOf(2249)), BigDecimal.valueOf(98.75));
        Assertions.assertEquals(nftService.getPrice(COLLECTION, BigInteger.valueOf(1875)), BigDecimal.valueOf(95));
        Assertions.assertEquals(nftService.getPrice(COLLECTION, BigInteger.valueOf(1109)), BigDecimal.valueOf(87));
    }

    @Test
    public void testAverageCalculation()
    {
        NftService nftService = getNftService(COLLECTION);
        var tokenId = BigInteger.valueOf(2577);

        nftService.AddPriceRecord(BigInteger.valueOf(8).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.ONE, tokenId, COLLECTION);
        nftService.AddPriceRecord(BigInteger.valueOf(27).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.TWO, tokenId, COLLECTION);
        nftService.AddPriceRecord(BigInteger.valueOf(32).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.valueOf(3), tokenId, COLLECTION);
        nftService.AddPriceRecord(BigInteger.valueOf(48).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.valueOf(4), tokenId, COLLECTION);
        Assertions.assertEquals(nftService.getPrice(COLLECTION, tokenId), BigDecimal.valueOf(28.75));
    }

    private NftService getNftService(String collection)
    {
        var nftService = new NftService(PROVIDER, BigInteger.valueOf(12175719), Set.of(collection), ignite);
        nftService.priceCache.clear();
        nftService.latestProcessedBlockCache.clear();
        return nftService;
    }

    private static Ignite getIgnite()
    {
        IgniteConfiguration cfg = new IgniteConfiguration();
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        storageCfg.getDefaultDataRegionConfiguration().setPersistenceEnabled(true);
        storageCfg.setStoragePath(STORAGE_PATH);
        cfg.setDataStorageConfiguration(storageCfg);
        Ignite ignite = Ignition.start(cfg);
        ignite.cluster().state(ClusterState.ACTIVE);
        return ignite;
    }
}
