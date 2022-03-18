package net.muon.data.nft.web3;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class OpenseaSale extends Contract
{
    public static final String BINARY = "Bin file was not provided";

    private static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}));
    public static final String TRANSFER_EVENT_ENCODED = EventEncoder.encode(TRANSFER_EVENT);

    private static final Event ORDERS_MATCHED_EVENT = new Event("OrdersMatched",
            Arrays.asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>(true) {}));
    public static final String ORDERS_MATCHED_EVENT_ENCODED = EventEncoder.encode(ORDERS_MATCHED_EVENT);

    private final List<String> collections;
    private final List<String> exchanges;

    public OpenseaSale(Set<String> exchanges, Set<String> collections, Web3j web3j)
    {
        super(BINARY, exchanges.stream().findFirst().get(), web3j, new ReadonlyTransactionManager(web3j, exchanges.stream().findFirst().get()), new DefaultGasProvider());
        this.collections = collections.stream().map(c -> c.toLowerCase(Locale.ROOT)).distinct().collect(Collectors.toList());
        this.exchanges = exchanges.stream().map(e -> e.toLowerCase(Locale.ROOT)).distinct().collect(Collectors.toList());
    }

    public List<TransferEventResponse> getTransferEvents(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) throws IOException
    {
        var filter = new EthFilter(startBlock, endBlock, collections);
        filter.addSingleTopic(TRANSFER_EVENT_ENCODED);
        var ethLogsResult = web3j.ethGetLogs(filter).send();
        if (ethLogsResult.hasError())
            throw createLogRequestError(ethLogsResult.getError());
        return ethLogsResult.getLogs().stream().map(logResult -> decodeTransferEvent(((Log) logResult))).collect(Collectors.toList());
    }

    public List<OrdersMatchedEventResponse> getOrdersMatchedEvents(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock, String[] makers, String[] takers) throws IOException
    {
        var filter = new EthFilter(startBlock, endBlock, exchanges);
        filter.addSingleTopic(ORDERS_MATCHED_EVENT_ENCODED);
        filter.addOptionalTopics(makers);
        filter.addOptionalTopics(takers);
        var ethLogsResult = web3j.ethGetLogs(filter).send();
        if (ethLogsResult.hasError())
            throw createLogRequestError(ethLogsResult.getError());
        return ethLogsResult.getLogs().stream().map(logResult -> decodeOrdersMatchedEvent(((Log) logResult))).collect(Collectors.toList());
    }

    private IOException createLogRequestError(Response.Error error)
    {
        if (error.getMessage().contains("more than 10000"))
            return new TooManyLogsException(error.getMessage());
        return new IOException(error.getMessage());
    }

    public List<SaleEventResponse> getEvents(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) throws IOException
    {
        var transferLogs = getTransferEvents(startBlock, endBlock);
        var makersTakersList = transferLogs.stream().map(log -> "0x" + TypeEncoder.encode(new Address(log.from))).collect(Collectors.toList());
        makersTakersList.addAll(transferLogs.stream().map(log -> "0x" + TypeEncoder.encode(new Address(log.to))).collect(Collectors.toList()));
        var makersTakersFilter = makersTakersList.toArray(String[]::new);
        var ordersMatchedLogs = getOrdersMatchedEvents(startBlock, endBlock, makersTakersFilter, makersTakersFilter);

        return transferLogs.stream().map(transferLog -> {
            var omLogs = ordersMatchedLogs.stream().filter(l -> l.log.getTransactionHash().equals(transferLog.log.getTransactionHash())).collect(Collectors.toList());
            if (omLogs.size() != 1)
                return null;
            return new SaleEventResponse(transferLog, omLogs.get(0));
        }).filter(Objects::nonNull).collect(Collectors.toList());
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

    private OrdersMatchedEventResponse decodeOrdersMatchedEvent(Log log)
    {
        var eventValues = extractEventParametersWithLog(ORDERS_MATCHED_EVENT, log);
        var typedResponse = new OrdersMatchedEventResponse();
        typedResponse.log = log;
        typedResponse.maker = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.taker = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.metadata = (byte[]) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.buyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.sellHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public static class SaleEventResponse
    {
        public TransferEventResponse transfer;
        public OrdersMatchedEventResponse ordersMatched;

        public SaleEventResponse(TransferEventResponse transfer, OrdersMatchedEventResponse ordersMatched)
        {
            this.transfer = transfer;
            this.ordersMatched = ordersMatched;
        }
    }

    public static class TransferEventResponse extends BaseEventResponse
    {
        public String from;
        public String to;
        public BigInteger tokenId;
    }

    public static class OrdersMatchedEventResponse extends BaseEventResponse
    {
        public String maker;
        public String taker;
        public byte[] metadata;
        public byte[] buyHash;
        public byte[] sellHash;
        public BigInteger price;
    }

    public class TooManyLogsException extends IOException
    {
        public TooManyLogsException(String message)
        {
            super(message);
        }
    }
}
