package net.muon.data.nft;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.8.4.
 */
@SuppressWarnings("rawtypes")
public class OpenseaWyvernExchange extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_TOKENTRANSFERPROXY = "tokenTransferProxy";

    public static final String FUNC_STATICCALL = "staticCall";

    public static final String FUNC_CHANGEMINIMUMMAKERPROTOCOLFEE = "changeMinimumMakerProtocolFee";

    public static final String FUNC_CHANGEMINIMUMTAKERPROTOCOLFEE = "changeMinimumTakerProtocolFee";

    public static final String FUNC_GUARDEDARRAYREPLACE = "guardedArrayReplace";

    public static final String FUNC_MINIMUMTAKERPROTOCOLFEE = "minimumTakerProtocolFee";

    public static final String FUNC_CODENAME = "codename";

    public static final String FUNC_TESTCOPYADDRESS = "testCopyAddress";

    public static final String FUNC_TESTCOPY = "testCopy";

    public static final String FUNC_CALCULATECURRENTPRICE_ = "calculateCurrentPrice_";

    public static final String FUNC_CHANGEPROTOCOLFEERECIPIENT = "changeProtocolFeeRecipient";

    public static final String FUNC_VERSION = "version";

    public static final String FUNC_ORDERCALLDATACANMATCH = "orderCalldataCanMatch";

    public static final String FUNC_VALIDATEORDER_ = "validateOrder_";

    public static final String FUNC_CALCULATEFINALPRICE = "calculateFinalPrice";

    public static final String FUNC_PROTOCOLFEERECIPIENT = "protocolFeeRecipient";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_HASHORDER_ = "hashOrder_";

    public static final String FUNC_ORDERSCANMATCH_ = "ordersCanMatch_";

    public static final String FUNC_APPROVEORDER_ = "approveOrder_";

    public static final String FUNC_REGISTRY = "registry";

    public static final String FUNC_MINIMUMMAKERPROTOCOLFEE = "minimumMakerProtocolFee";

    public static final String FUNC_HASHTOSIGN_ = "hashToSign_";

    public static final String FUNC_CANCELLEDORFINALIZED = "cancelledOrFinalized";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_EXCHANGETOKEN = "exchangeToken";

    public static final String FUNC_CANCELORDER_ = "cancelOrder_";

    public static final String FUNC_ATOMICMATCH_ = "atomicMatch_";

    public static final String FUNC_VALIDATEORDERPARAMETERS_ = "validateOrderParameters_";

    public static final String FUNC_INVERSE_BASIS_POINT = "INVERSE_BASIS_POINT";

    public static final String FUNC_CALCULATEMATCHPRICE_ = "calculateMatchPrice_";

    public static final String FUNC_APPROVEDORDERS = "approvedOrders";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event ORDERAPPROVEDPARTONE_EVENT = new Event("OrderApprovedPartOne", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>() {}, new TypeReference<Address>(true) {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>(true) {}, new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event ORDERAPPROVEDPARTTWO_EVENT = new Event("OrderApprovedPartTwo", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Uint8>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event ORDERCANCELLED_EVENT = new Event("OrderCancelled", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event ORDERSMATCHED_EVENT = new Event("OrdersMatched", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event OWNERSHIPRENOUNCED_EVENT = new Event("OwnershipRenounced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected OpenseaWyvernExchange(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected OpenseaWyvernExchange(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected OpenseaWyvernExchange(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected OpenseaWyvernExchange(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> tokenTransferProxy() {
        final Function function = new Function(FUNC_TOKENTRANSFERPROXY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> staticCall(String target, byte[] calldata, byte[] extradata) {
        final Function function = new Function(FUNC_STATICCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, target), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(extradata)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> changeMinimumMakerProtocolFee(BigInteger newMinimumMakerProtocolFee) {
        final Function function = new Function(
                FUNC_CHANGEMINIMUMMAKERPROTOCOLFEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(newMinimumMakerProtocolFee)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> changeMinimumTakerProtocolFee(BigInteger newMinimumTakerProtocolFee) {
        final Function function = new Function(
                FUNC_CHANGEMINIMUMTAKERPROTOCOLFEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(newMinimumTakerProtocolFee)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> guardedArrayReplace(byte[] array, byte[] desired, byte[] mask) {
        final Function function = new Function(FUNC_GUARDEDARRAYREPLACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(array), 
                new org.web3j.abi.datatypes.DynamicBytes(desired), 
                new org.web3j.abi.datatypes.DynamicBytes(mask)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> minimumTakerProtocolFee() {
        final Function function = new Function(FUNC_MINIMUMTAKERPROTOCOLFEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> codename() {
        final Function function = new Function(FUNC_CODENAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<byte[]> testCopyAddress(String addr) {
        final Function function = new Function(FUNC_TESTCOPYADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> testCopy(byte[] arrToCopy) {
        final Function function = new Function(FUNC_TESTCOPY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(arrToCopy)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> calculateCurrentPrice_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata) {
        final Function function = new Function(FUNC_CALCULATECURRENTPRICE_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> changeProtocolFeeRecipient(String newProtocolFeeRecipient) {
        final Function function = new Function(
                FUNC_CHANGEPROTOCOLFEERECIPIENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newProtocolFeeRecipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> version() {
        final Function function = new Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> orderCalldataCanMatch(byte[] buyCalldata, byte[] buyReplacementPattern, byte[] sellCalldata, byte[] sellReplacementPattern) {
        final Function function = new Function(FUNC_ORDERCALLDATACANMATCH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(buyCalldata), 
                new org.web3j.abi.datatypes.DynamicBytes(buyReplacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(sellCalldata), 
                new org.web3j.abi.datatypes.DynamicBytes(sellReplacementPattern)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> validateOrder_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata, BigInteger v, byte[] r, byte[] s) {
        final Function function = new Function(FUNC_VALIDATEORDER_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> calculateFinalPrice(BigInteger side, BigInteger saleKind, BigInteger basePrice, BigInteger extra, BigInteger listingTime, BigInteger expirationTime) {
        final Function function = new Function(FUNC_CALCULATEFINALPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint256(basePrice), 
                new org.web3j.abi.datatypes.generated.Uint256(extra), 
                new org.web3j.abi.datatypes.generated.Uint256(listingTime), 
                new org.web3j.abi.datatypes.generated.Uint256(expirationTime)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> protocolFeeRecipient() {
        final Function function = new Function(FUNC_PROTOCOLFEERECIPIENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> hashOrder_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata) {
        final Function function = new Function(FUNC_HASHORDER_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Boolean> ordersCanMatch_(List<String> addrs, List<BigInteger> uints, List<BigInteger> feeMethodsSidesKindsHowToCalls, byte[] calldataBuy, byte[] calldataSell, byte[] replacementPatternBuy, byte[] replacementPatternSell, byte[] staticExtradataBuy, byte[] staticExtradataSell) {
        final Function function = new Function(FUNC_ORDERSCANMATCH_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray14<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray18<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray8<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(feeMethodsSidesKindsHowToCalls, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(calldataBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(calldataSell), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPatternBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPatternSell), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradataBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradataSell)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approveOrder_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata, Boolean orderbookInclusionDesired) {
        final Function function = new Function(
                FUNC_APPROVEORDER_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata), 
                new org.web3j.abi.datatypes.Bool(orderbookInclusionDesired)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> registry() {
        final Function function = new Function(FUNC_REGISTRY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> minimumMakerProtocolFee() {
        final Function function = new Function(FUNC_MINIMUMMAKERPROTOCOLFEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> hashToSign_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata) {
        final Function function = new Function(FUNC_HASHTOSIGN_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Boolean> cancelledOrFinalized(byte[] param0) {
        final Function function = new Function(FUNC_CANCELLEDORFINALIZED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> exchangeToken() {
        final Function function = new Function(FUNC_EXCHANGETOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> cancelOrder_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata, BigInteger v, byte[] r, byte[] s) {
        final Function function = new Function(
                FUNC_CANCELORDER_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> atomicMatch_(List<String> addrs, List<BigInteger> uints, List<BigInteger> feeMethodsSidesKindsHowToCalls, byte[] calldataBuy, byte[] calldataSell, byte[] replacementPatternBuy, byte[] replacementPatternSell, byte[] staticExtradataBuy, byte[] staticExtradataSell, List<BigInteger> vs, List<byte[]> rssMetadata, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_ATOMICMATCH_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray14<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray18<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray8<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(feeMethodsSidesKindsHowToCalls, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(calldataBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(calldataSell), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPatternBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPatternSell), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradataBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradataSell), 
                new org.web3j.abi.datatypes.generated.StaticArray2<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(vs, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray5<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(rssMetadata, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<Boolean> validateOrderParameters_(List<String> addrs, List<BigInteger> uints, BigInteger feeMethod, BigInteger side, BigInteger saleKind, BigInteger howToCall, byte[] calldata, byte[] replacementPattern, byte[] staticExtradata) {
        final Function function = new Function(FUNC_VALIDATEORDERPARAMETERS_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray7<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray9<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(feeMethod), 
                new org.web3j.abi.datatypes.generated.Uint8(side), 
                new org.web3j.abi.datatypes.generated.Uint8(saleKind), 
                new org.web3j.abi.datatypes.generated.Uint8(howToCall), 
                new org.web3j.abi.datatypes.DynamicBytes(calldata), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPattern), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradata)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> INVERSE_BASIS_POINT() {
        final Function function = new Function(FUNC_INVERSE_BASIS_POINT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> calculateMatchPrice_(List<String> addrs, List<BigInteger> uints, List<BigInteger> feeMethodsSidesKindsHowToCalls, byte[] calldataBuy, byte[] calldataSell, byte[] replacementPatternBuy, byte[] replacementPatternSell, byte[] staticExtradataBuy, byte[] staticExtradataSell) {
        final Function function = new Function(FUNC_CALCULATEMATCHPRICE_, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.StaticArray14<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addrs, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray18<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(uints, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.StaticArray8<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(feeMethodsSidesKindsHowToCalls, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(calldataBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(calldataSell), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPatternBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(replacementPatternSell), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradataBuy), 
                new org.web3j.abi.datatypes.DynamicBytes(staticExtradataSell)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> approvedOrders(byte[] param0) {
        final Function function = new Function(FUNC_APPROVEDORDERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<OrderApprovedPartOneEventResponse> getOrderApprovedPartOneEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ORDERAPPROVEDPARTONE_EVENT, transactionReceipt);
        ArrayList<OrderApprovedPartOneEventResponse> responses = new ArrayList<OrderApprovedPartOneEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OrderApprovedPartOneEventResponse typedResponse = new OrderApprovedPartOneEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.hash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.maker = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.feeRecipient = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.exchange = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.taker = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.makerRelayerFee = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.takerRelayerFee = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.makerProtocolFee = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.takerProtocolFee = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.feeMethod = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
            typedResponse.side = (BigInteger) eventValues.getNonIndexedValues().get(7).getValue();
            typedResponse.saleKind = (BigInteger) eventValues.getNonIndexedValues().get(8).getValue();
            typedResponse.target = (String) eventValues.getNonIndexedValues().get(9).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OrderApprovedPartOneEventResponse> orderApprovedPartOneEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OrderApprovedPartOneEventResponse>() {
            @Override
            public OrderApprovedPartOneEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ORDERAPPROVEDPARTONE_EVENT, log);
                OrderApprovedPartOneEventResponse typedResponse = new OrderApprovedPartOneEventResponse();
                typedResponse.log = log;
                typedResponse.hash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.maker = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.feeRecipient = (String) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.exchange = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.taker = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.makerRelayerFee = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.takerRelayerFee = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.makerProtocolFee = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.takerProtocolFee = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.feeMethod = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
                typedResponse.side = (BigInteger) eventValues.getNonIndexedValues().get(7).getValue();
                typedResponse.saleKind = (BigInteger) eventValues.getNonIndexedValues().get(8).getValue();
                typedResponse.target = (String) eventValues.getNonIndexedValues().get(9).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OrderApprovedPartOneEventResponse> orderApprovedPartOneEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ORDERAPPROVEDPARTONE_EVENT));
        return orderApprovedPartOneEventFlowable(filter);
    }

    public List<OrderApprovedPartTwoEventResponse> getOrderApprovedPartTwoEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ORDERAPPROVEDPARTTWO_EVENT, transactionReceipt);
        ArrayList<OrderApprovedPartTwoEventResponse> responses = new ArrayList<OrderApprovedPartTwoEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OrderApprovedPartTwoEventResponse typedResponse = new OrderApprovedPartTwoEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.hash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.howToCall = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.calldata = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.replacementPattern = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.staticTarget = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.staticExtradata = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.paymentToken = (String) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.basePrice = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
            typedResponse.extra = (BigInteger) eventValues.getNonIndexedValues().get(7).getValue();
            typedResponse.listingTime = (BigInteger) eventValues.getNonIndexedValues().get(8).getValue();
            typedResponse.expirationTime = (BigInteger) eventValues.getNonIndexedValues().get(9).getValue();
            typedResponse.salt = (BigInteger) eventValues.getNonIndexedValues().get(10).getValue();
            typedResponse.orderbookInclusionDesired = (Boolean) eventValues.getNonIndexedValues().get(11).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OrderApprovedPartTwoEventResponse> orderApprovedPartTwoEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OrderApprovedPartTwoEventResponse>() {
            @Override
            public OrderApprovedPartTwoEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ORDERAPPROVEDPARTTWO_EVENT, log);
                OrderApprovedPartTwoEventResponse typedResponse = new OrderApprovedPartTwoEventResponse();
                typedResponse.log = log;
                typedResponse.hash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.howToCall = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.calldata = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.replacementPattern = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.staticTarget = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.staticExtradata = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.paymentToken = (String) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.basePrice = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
                typedResponse.extra = (BigInteger) eventValues.getNonIndexedValues().get(7).getValue();
                typedResponse.listingTime = (BigInteger) eventValues.getNonIndexedValues().get(8).getValue();
                typedResponse.expirationTime = (BigInteger) eventValues.getNonIndexedValues().get(9).getValue();
                typedResponse.salt = (BigInteger) eventValues.getNonIndexedValues().get(10).getValue();
                typedResponse.orderbookInclusionDesired = (Boolean) eventValues.getNonIndexedValues().get(11).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OrderApprovedPartTwoEventResponse> orderApprovedPartTwoEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ORDERAPPROVEDPARTTWO_EVENT));
        return orderApprovedPartTwoEventFlowable(filter);
    }

    public List<OrderCancelledEventResponse> getOrderCancelledEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ORDERCANCELLED_EVENT, transactionReceipt);
        ArrayList<OrderCancelledEventResponse> responses = new ArrayList<OrderCancelledEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OrderCancelledEventResponse typedResponse = new OrderCancelledEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.hash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OrderCancelledEventResponse> orderCancelledEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OrderCancelledEventResponse>() {
            @Override
            public OrderCancelledEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ORDERCANCELLED_EVENT, log);
                OrderCancelledEventResponse typedResponse = new OrderCancelledEventResponse();
                typedResponse.log = log;
                typedResponse.hash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OrderCancelledEventResponse> orderCancelledEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ORDERCANCELLED_EVENT));
        return orderCancelledEventFlowable(filter);
    }

    public List<OrdersMatchedEventResponse> getOrdersMatchedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ORDERSMATCHED_EVENT, transactionReceipt);
        ArrayList<OrdersMatchedEventResponse> responses = new ArrayList<OrdersMatchedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OrdersMatchedEventResponse typedResponse = new OrdersMatchedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.maker = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.taker = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.metadata = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.buyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sellHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OrdersMatchedEventResponse> ordersMatchedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OrdersMatchedEventResponse>() {
            @Override
            public OrdersMatchedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ORDERSMATCHED_EVENT, log);
                OrdersMatchedEventResponse typedResponse = new OrdersMatchedEventResponse();
                typedResponse.log = log;
                typedResponse.maker = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.taker = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.metadata = (byte[]) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.buyHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.sellHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OrdersMatchedEventResponse> ordersMatchedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ORDERSMATCHED_EVENT));
        return ordersMatchedEventFlowable(filter);
    }

    public List<OwnershipRenouncedEventResponse> getOwnershipRenouncedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPRENOUNCED_EVENT, transactionReceipt);
        ArrayList<OwnershipRenouncedEventResponse> responses = new ArrayList<OwnershipRenouncedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipRenouncedEventResponse typedResponse = new OwnershipRenouncedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipRenouncedEventResponse> ownershipRenouncedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OwnershipRenouncedEventResponse>() {
            @Override
            public OwnershipRenouncedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPRENOUNCED_EVENT, log);
                OwnershipRenouncedEventResponse typedResponse = new OwnershipRenouncedEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipRenouncedEventResponse> ownershipRenouncedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPRENOUNCED_EVENT));
        return ownershipRenouncedEventFlowable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    @Deprecated
    public static OpenseaWyvernExchange load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new OpenseaWyvernExchange(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static OpenseaWyvernExchange load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new OpenseaWyvernExchange(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static OpenseaWyvernExchange load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new OpenseaWyvernExchange(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static OpenseaWyvernExchange load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new OpenseaWyvernExchange(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class OrderApprovedPartOneEventResponse extends BaseEventResponse {
        public byte[] hash;

        public String maker;

        public String feeRecipient;

        public String exchange;

        public String taker;

        public BigInteger makerRelayerFee;

        public BigInteger takerRelayerFee;

        public BigInteger makerProtocolFee;

        public BigInteger takerProtocolFee;

        public BigInteger feeMethod;

        public BigInteger side;

        public BigInteger saleKind;

        public String target;
    }

    public static class OrderApprovedPartTwoEventResponse extends BaseEventResponse {
        public byte[] hash;

        public BigInteger howToCall;

        public byte[] calldata;

        public byte[] replacementPattern;

        public String staticTarget;

        public byte[] staticExtradata;

        public String paymentToken;

        public BigInteger basePrice;

        public BigInteger extra;

        public BigInteger listingTime;

        public BigInteger expirationTime;

        public BigInteger salt;

        public Boolean orderbookInclusionDesired;
    }

    public static class OrderCancelledEventResponse extends BaseEventResponse {
        public byte[] hash;
    }

    public static class OrdersMatchedEventResponse extends BaseEventResponse {
        public String maker;

        public String taker;

        public byte[] metadata;

        public byte[] buyHash;

        public byte[] sellHash;

        public BigInteger price;
    }

    public static class OwnershipRenouncedEventResponse extends BaseEventResponse {
        public String previousOwner;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
