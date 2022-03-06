package net.muon.data.nft;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class ContractTest
{

    private static final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
    private static final String OPENSEA_CONTRACT_ADDRESS = "0x7be8076f4ea4a4ad08075c2508e481d6c946d12b";
    private static final OpenseaWyvernExchange opensea = OpenseaWyvernExchange.load(OPENSEA_CONTRACT_ADDRESS, web3j, new ReadonlyTransactionManager(web3j, OPENSEA_CONTRACT_ADDRESS), new DefaultGasProvider());

    private static final String BORED_APE_COLLECTION_CONTRACT_ADDRESS = "0xBC4CA0EdA7647A8aB7C2061c2E118A18a936f13D";
    private static final ERC721Ex boredApe = ERC721Ex.load(BORED_APE_COLLECTION_CONTRACT_ADDRESS, web3j, new ReadonlyTransactionManager(web3j, BORED_APE_COLLECTION_CONTRACT_ADDRESS), new DefaultGasProvider());

    public static void main(String[] args) throws IOException
    {
//        testOpensea();
        testErc721();
    }

    private static void testErc721() throws IOException
    {
        var transferLogs = boredApe.getTransferEvents(DefaultBlockParameter.valueOf(BigInteger.valueOf(14332848)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(14333057)));
        for (var l : transferLogs) {
            System.out.printf("%s: %s%n", l.log.getTransactionHash(), l.tokenId);
        }
    }

    private static void testOpensea() throws IOException
    {
        Optional<TransactionReceipt> transactionReceipt =
                web3j.ethGetTransactionReceipt("0xaf8ac4969f56148ae37d9603e59a32a79a69c36c0792b50a1d3dd77953cb246c").send().getTransactionReceipt();
        if (transactionReceipt.isPresent()) {
            var logs = opensea.getOrdersMatchedEvents(transactionReceipt.get());
        }
    }
}
