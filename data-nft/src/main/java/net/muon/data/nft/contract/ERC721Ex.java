package net.muon.data.nft.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class ERC721Ex extends ERC721
{
    public ERC721Ex(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider)
    {
        super(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    private TransferEventResponse decodeTransferEvent(Log log)
    {
        var eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
        var typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public List<TransferEventResponse> getTransferEvents(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock, List<String> contracts) throws IOException
    {
        var filter = new EthFilter(startBlock, endBlock, contracts);
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return web3j.ethGetLogs(filter).send().getLogs().stream().map(logResult -> decodeTransferEvent((Log) logResult)).collect(Collectors.toList());
    }

    public static ERC721Ex load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider)
    {
        return new ERC721Ex(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
