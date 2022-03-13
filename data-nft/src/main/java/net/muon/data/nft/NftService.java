package net.muon.data.nft;

import net.muon.data.nft.contract.ERC721Ex;
import net.muon.data.nft.contract.OpenseaWyvernExchange;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

public class NftService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NftService.class);
    private static final String OPENSEA_CONTRACT_ADDRESS = "0x7be8076f4ea4a4ad08075c2508e481d6c946d12b";
    private static final BigInteger ALLOWED_BLOCK_HEIGHT = BigInteger.valueOf(10000);
    private static final String PRICE_CACHE = "nft_token_price_cache";
    private static final String LATEST_BLOCK_CACHE = "nft_token_latest_processed_block_cache";
    private static final String LATEST_BLOCK = "latest_block";
    protected static final BigDecimal ETH_IN_WEI = BigDecimal.valueOf(1000000000000000000L);
    private static final MathContext PRECISION = new MathContext(5);


    private final Web3j web3;
    private final ERC721Ex erc721;
    private final OpenseaWyvernExchange opensea;
    private final List<String> collections;
    protected final IgniteCache<String, Map<BigInteger, TokenPrice>> priceCache; // collection:tokenId -> blockNumber -> TokenPrice (price, avg, count)
    protected final IgniteCache<String, BigInteger> latestProcessedBlockCache;

    private BigInteger nextBlockNumber;

    public NftService(String web3Provider, BigInteger startingBlockNumber, Set<String> collections, Ignite ignite)
    {

        this.collections = collections.stream().map(a -> a.toLowerCase(Locale.ROOT)).distinct().collect(Collectors.toList());
        web3 = Web3j.build(new HttpService(web3Provider));
        opensea = OpenseaWyvernExchange.load(OPENSEA_CONTRACT_ADDRESS, web3, new ReadonlyTransactionManager(web3, OPENSEA_CONTRACT_ADDRESS), new DefaultGasProvider());
        erc721 = ERC721Ex.load(this.collections.get(0), web3, new ReadonlyTransactionManager(web3, this.collections.get(0)), new DefaultGasProvider()); // FIXME

        var config = new CacheConfiguration<String, Map<BigInteger, TokenPrice>>(PRICE_CACHE);
        config.setCacheMode(CacheMode.REPLICATED); // FIXME ?
        this.priceCache = ignite.getOrCreateCache(config);

        var latestBlockConfig = new CacheConfiguration<String, BigInteger>(LATEST_BLOCK_CACHE);
        latestBlockConfig.setCacheMode(CacheMode.REPLICATED);
        this.latestProcessedBlockCache = ignite.getOrCreateCache(latestBlockConfig);

        nextBlockNumber = startingBlockNumber.max(getCacheLastProcessedBlockNumber().add(BigInteger.ONE));
    }

    private BigInteger getCacheLastProcessedBlockNumber()
    {
        var latestBlock = latestProcessedBlockCache.get(LATEST_BLOCK);
        return latestBlock != null ? latestBlock : BigInteger.ZERO;
    }

    // TODO: schedule
    public void ScanSales()
    {
        BigInteger currentBlockNumber;
        try {
            currentBlockNumber = web3.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            LOGGER.warn("Exception suppressed", e);
            return;
        }

        if (currentBlockNumber.compareTo(nextBlockNumber) < 0)
            return;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scanning sale events");

        try {
            handleSaleLogs(nextBlockNumber, currentBlockNumber);
        } catch (IOException e) {
            LOGGER.warn("Exception suppressed", e);
        }
    }

    void handleSaleLogs(BigInteger fromBlock, BigInteger toBlock) throws IOException
    {
        do {
            for (var transferLog : erc721.getTransferEvents(DefaultBlockParameter.valueOf(fromBlock),
                    DefaultBlockParameter.valueOf(toBlock.min(fromBlock.add(ALLOWED_BLOCK_HEIGHT))), collections)) {
                var transactionReceipt = web3.ethGetTransactionReceipt(transferLog.log.getTransactionHash()).send().getTransactionReceipt();
                if (transactionReceipt.isPresent()) {
                    var ordersMatchedLogs = opensea.getOrdersMatchedEvents(transactionReceipt.get());
                    if (ordersMatchedLogs.size() != 1) continue;
                    AddPriceRecord(ordersMatchedLogs.get(0).price, transferLog.log.getBlockNumber(),
                            transferLog.tokenId, transferLog.log.getAddress().toLowerCase(Locale.ROOT));
                    UpdateNextBlockNumber(transferLog.log.getBlockNumber());
                }
            }
            fromBlock = fromBlock.add(ALLOWED_BLOCK_HEIGHT);
        }
        while (toBlock.subtract(fromBlock).compareTo(BigInteger.ZERO) > 0);
    }

    protected void AddPriceRecord(BigInteger price, BigInteger blockNumber, BigInteger tokenId, String collection) // TODO: add average price cache
    {
        var collectionToken = GetTokenIdentifier(collection, tokenId);

        var previousBlock = latestProcessedBlockCache.get(collectionToken);
        if (previousBlock == null) {
            var priceMap = new HashMap<BigInteger, TokenPrice>();
            priceMap.put(blockNumber, new TokenPrice(price, price, BigInteger.ONE));
            priceCache.put(collectionToken, priceMap);
            latestProcessedBlockCache.put(collectionToken, blockNumber);

            LOGGER.debug("{}: first record inserted", collectionToken);
        } else {
            var priceMap = priceCache.get(collectionToken);
            var previousPrice = priceMap.get(previousBlock); // TODO: null check?
            priceMap.put(blockNumber, previousPrice.CalculateNext(price));
            priceCache.put(collectionToken, priceMap);

            LOGGER.debug("{}: record number {} inserted", collectionToken, priceMap.size());
        }

        latestProcessedBlockCache.put(collectionToken, blockNumber);
    }

    private void UpdateNextBlockNumber(BigInteger blockNumber)
    {
        nextBlockNumber = blockNumber.add(BigInteger.ONE);
        latestProcessedBlockCache.put(LATEST_BLOCK, blockNumber);
    }

    public BigDecimal getPrice(String collectionId, BigInteger nftId)
    {
        var collectionToken = GetTokenIdentifier(collectionId, nftId);
        var latestBlock = latestProcessedBlockCache.get(collectionToken);
        if (latestBlock == null)
            return null; // TODO: no sale records

        var priceMap = priceCache.get(collectionToken); // TODO: null check?
        var latestRecord = priceMap.get(latestBlock);

        return new BigDecimal(latestRecord.getCumulativePrice()).divide(new BigDecimal(latestRecord.getCount()), PRECISION)
                .divide(ETH_IN_WEI, PRECISION);
    }

    private static String GetTokenIdentifier(String collection, BigInteger id)
    {
        return String.format("%s:%s", collection.toLowerCase(Locale.ROOT), id.toString());
    }
}
