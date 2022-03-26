package net.muon.data.nft.web3;

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
    public static final String COLLECTION = "0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d";
    private static final String PROVIDER = "https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161";
    private static final String STORAGE_PATH = "/tmp/muon-data";
    private static final Ignite ignite = getIgnite();

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
    public void testSingleToken()
    {
        var start = BigInteger.valueOf(13110840);
        NftService nftService = getNftService(COLLECTION, start);
        var end = BigInteger.valueOf(13167727);

        while (nftService.nextBlockNumber.compareTo(end) <= 0) {
            nftService.scanSalesTo(end);
        }
        Assertions.assertEquals(nftService.getPrice(COLLECTION, BigInteger.valueOf(3368)), BigDecimal.valueOf(34));
    }

    @Test
    public void testAverageCalculation()
    {
        NftService nftService = getNftService(COLLECTION);
        var tokenId = BigInteger.valueOf(2577);

        nftService.addPriceRecord(BigInteger.valueOf(8).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.ONE, tokenId, COLLECTION);
        nftService.addPriceRecord(BigInteger.valueOf(27).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.TWO, tokenId, COLLECTION);
        nftService.addPriceRecord(BigInteger.valueOf(32).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.valueOf(3), tokenId, COLLECTION);
        nftService.addPriceRecord(BigInteger.valueOf(48).multiply(NftService.ETH_IN_WEI.toBigInteger()), BigInteger.valueOf(4), tokenId, COLLECTION);
        Assertions.assertEquals(nftService.getPrice(COLLECTION, tokenId), BigDecimal.valueOf(28.75));
    }

    private NftService getNftService(String collection)
    {
        return getNftService(collection, BigInteger.ZERO);
    }

    private NftService getNftService(String collection, BigInteger startingBlockNumber)
    {
        var nftService = new NftService(PROVIDER, startingBlockNumber, Set.of(collection), ignite);
        nftService.priceCache.clear();
        nftService.latestProcessedBlockCache.clear();
        nftService.nextBlockNumber = startingBlockNumber;
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
