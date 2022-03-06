package net.muon.data.nft;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class ContractTest
{

    private static final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
    private static final String OPENSEA_CONTRACT_ADDRESS = "0x7be8076f4ea4a4ad08075c2508e481d6c946d12b";
    private static final OpenseaWyvernExchange opensea = OpenseaWyvernExchange.load(OPENSEA_CONTRACT_ADDRESS, web3j, new ReadonlyTransactionManager(web3j, OPENSEA_CONTRACT_ADDRESS), new DefaultGasProvider());

    private static final String BORED_APE_COLLECTION_CONTRACT_ADDRESS = "0xBC4CA0EdA7647A8aB7C2061c2E118A18a936f13D";
    private static final ERC721 boredApe = ERC721.load(BORED_APE_COLLECTION_CONTRACT_ADDRESS, web3j, new ReadonlyTransactionManager(web3j, BORED_APE_COLLECTION_CONTRACT_ADDRESS), new DefaultGasProvider());

    public static void main(String[] args)
    {
//        testOpensea();
        testErc721();
    }

    private static void testErc721()
    {
        boredApe.transferEventFlowable(DefaultBlockParameter.valueOf(BigInteger.valueOf(13907660)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(13907660))).subscribe(transferEventResponse -> {
            System.out.println(transferEventResponse.log.getTransactionHash() + ": " + transferEventResponse.tokenId);
        });
    }

    private static void testOpensea()
    {
        opensea.ordersMatchedEventFlowable(DefaultBlockParameter.valueOf(BigInteger.valueOf(14232064)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(14232064))).subscribe(ordersMatchedEventResponse -> {
            System.out.println(ordersMatchedEventResponse.log.getTransactionHash() + ": " + ordersMatchedEventResponse.price);
        });
    }
}
