package net.muon.data.nft.web3;

import com.google.common.base.Preconditions;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class NftService
{
    protected static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final Logger LOGGER = LoggerFactory.getLogger(NftService.class);
    private static final Set<String> OPENSEA_EXCHANGES = Set.of("0x7be8076f4ea4a4ad08075c2508e481d6c946d12b", "0x7f268357a8c2552623316e2562d90e642bb538e5");
    private static final String PRICE_CACHE = "nft_token_price_cache";
    private static final String LATEST_BLOCK_CACHE = "nft_token_latest_processed_block_cache";
    private static final String LATEST_BLOCK = "latest_block";
    private static final MathContext PRECISION = new MathContext(5);
    protected final IgniteCache<String, Map<BigInteger, TokenPrice>> priceCache; // collection:tokenId -> blockNumber -> TokenPrice (price, avg, count)
    protected final IgniteCache<String, BigInteger> latestProcessedBlockCache;
    private final Web3j web3;
    private final OpenseaSale openseaSale;
    protected BigInteger nextBlockNumber;
    private BigInteger allowedBlockHeight = BigInteger.valueOf(10000);

    public NftService(String web3Provider, BigInteger startingBlockNumber, Set<String> collections, Ignite ignite)
    {
        Preconditions.checkArgument(collections != null && !collections.isEmpty());
        web3 = Web3j.build(new HttpService(web3Provider));
        openseaSale = new OpenseaSale(OPENSEA_EXCHANGES, collections, web3);

        var config = new CacheConfiguration<String, Map<BigInteger, TokenPrice>>(PRICE_CACHE);
        config.setCacheMode(CacheMode.REPLICATED);
        this.priceCache = ignite.getOrCreateCache(config);

        var latestBlockConfig = new CacheConfiguration<String, BigInteger>(LATEST_BLOCK_CACHE);
        latestBlockConfig.setCacheMode(CacheMode.REPLICATED);
        this.latestProcessedBlockCache = ignite.getOrCreateCache(latestBlockConfig);

        nextBlockNumber = startingBlockNumber.max(getCacheLastProcessedBlockNumber().add(BigInteger.ONE));
    }

    private static String getTokenIdentifier(String collection, BigInteger id)
    {
        return String.format("%s:%s", collection.toLowerCase(Locale.ROOT), id.toString());
    }

    private BigInteger getCacheLastProcessedBlockNumber()
    {
        var latestBlock = latestProcessedBlockCache.get(LATEST_BLOCK);
        return latestBlock != null ? latestBlock : BigInteger.ZERO;
    }

    public void scanSales()
    {
        BigInteger currentBlockNumber;
        try {
            currentBlockNumber = web3.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            LOGGER.warn("Exception suppressed", e);
            return;
        }
        scanSalesTo(currentBlockNumber);
    }

    protected void scanSalesTo(BigInteger toBlock)
    {
        if (toBlock.compareTo(nextBlockNumber) < 0)
            return;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scanning sale events");

        try {
            handleSaleLogs(nextBlockNumber, toBlock);
        } catch (IOException e) {
            LOGGER.warn("Exception suppressed", e);
        }
    }

    void handleSaleLogs(BigInteger fromBlock, BigInteger toBlock) throws IOException
    {
        try {
            do {
                for (var saleLog : openseaSale.getEvents(DefaultBlockParameter.valueOf(fromBlock),
                        DefaultBlockParameter.valueOf(toBlock.min(fromBlock.add(allowedBlockHeight))))) {
                    var blockNumber = saleLog.transfer.log.getBlockNumber();
                    addPriceRecord(saleLog.ordersMatched.price, blockNumber, saleLog.transfer.tokenId, saleLog.transfer.log.getAddress());
                    updateNextBlockNumber(blockNumber);
                }
                fromBlock = fromBlock.add(allowedBlockHeight);
            }
            while (toBlock.subtract(fromBlock).compareTo(BigInteger.ZERO) > 0);
        } catch (OpenseaSale.TooManyLogsException e) {
            if (allowedBlockHeight.compareTo(BigInteger.TWO) > 0) {
                allowedBlockHeight = allowedBlockHeight.divide(BigInteger.TWO);
                LOGGER.debug("Allowed block height updated to {}", allowedBlockHeight);
            }
            throw e;
        }
    }

    protected void addPriceRecord(BigInteger price, BigInteger blockNumber, BigInteger tokenId, String collection)
    {
        var collectionToken = getTokenIdentifier(collection, tokenId);

        var previousBlock = latestProcessedBlockCache.get(collectionToken);
        if (previousBlock == null) {
            var priceMap = new HashMap<BigInteger, TokenPrice>();
            priceMap.put(blockNumber, new TokenPrice(price, price, BigInteger.ONE));
            priceCache.put(collectionToken, priceMap);
            latestProcessedBlockCache.put(collectionToken, blockNumber);

            LOGGER.debug("{}: first record inserted (price: {})", collectionToken, price);
        } else {
            var priceMap = priceCache.get(collectionToken);
            var previousPrice = priceMap.get(previousBlock); // TODO: null check?
            priceMap.put(blockNumber, previousPrice.CalculateNext(price));
            priceCache.put(collectionToken, priceMap);

            LOGGER.debug("{}: record number {} inserted (price: {})", collectionToken, priceMap.size(), price);
        }

        latestProcessedBlockCache.put(collectionToken, blockNumber);
    }

    private void updateNextBlockNumber(BigInteger blockNumber)
    {
        nextBlockNumber = blockNumber.add(BigInteger.ONE);
        latestProcessedBlockCache.put(LATEST_BLOCK, blockNumber);
    }

    public BigDecimal getPrice(String collectionId, BigInteger nftId)
    {
        var collectionToken = getTokenIdentifier(collectionId, nftId);
        var latestBlock = latestProcessedBlockCache.get(collectionToken);
        if (latestBlock == null)
            return null;

        var priceMap = priceCache.get(collectionToken); // TODO: null check?
        var latestRecord = priceMap.get(latestBlock);

        return new BigDecimal(latestRecord.getCumulativePrice()).divide(new BigDecimal(latestRecord.getCount()), PRECISION)
                .divide(ETH_IN_WEI, PRECISION);
    }
}
