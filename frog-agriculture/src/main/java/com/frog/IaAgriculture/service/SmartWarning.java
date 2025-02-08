package com.frog.IaAgriculture.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple9;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class SmartWarning extends Contract {
    public static final String[] BINARY_ARRAY = {"6080604052601e6002556032600355600160045534801561001f57600080fd5b506108288061002f6000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063b04322ac1161005b578063b04322ac146101ca578063eb03c7c91461020c578063ed5c0fd114610318578063fb44d377146103365761007d565b806324432ebc14610082578063700e07ce146100a05780637599be5d146101ac575b600080fd5b61008a610354565b6040518082815260200191505060405180910390f35b6101aa60048036036101208110156100b757600080fd5b81019080803590602001906401000000008111156100d457600080fd5b8201836020820111156100e657600080fd5b8035906020019184600183028401116401000000008311171561010857600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919050505061035a565b005b6101b461062e565b6040518082815260200191505060405180910390f35b61020a600480360360608110156101e057600080fd5b81019080803590602001909291908035906020019092919080359060200190929190505050610634565b005b610316600480360361012081101561022357600080fd5b810190808035906020019064010000000081111561024057600080fd5b82018360208201111561025257600080fd5b8035906020019184600183028401116401000000008311171561027457600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919050505061064e565b005b610320610741565b6040518082815260200191505060405180910390f35b61033e610747565b6040518082815260200191505060405180910390f35b60045481565b61036b89898989898989898961064e565b600254881315610453577ffb5777f0c4f49b4cfa3f4ab45ad5d512cc979e23f681ef4bb18907a121baa460428a6040518080602001848152602001806020018381038352601d8152602001807f54656d70657261747572652065786365656473205468726573686f6c64000000815250602001838103825284818151815260200191508051906020019080838360005b838110156104165780820151818401526020810190506103fb565b50505050905090810190601f1680156104435780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15b60035487111561053b577ffb5777f0c4f49b4cfa3f4ab45ad5d512cc979e23f681ef4bb18907a121baa460428a604051808060200184815260200180602001838103835260188152602001807f48756d69646974792062656c6f77205468726573686f6c640000000000000000815250602001838103825284818151815260200191508051906020019080838360005b838110156104fe5780820151818401526020810190506104e3565b50505050905090810190601f16801561052b5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15b600454811115610623577ffb5777f0c4f49b4cfa3f4ab45ad5d512cc979e23f681ef4bb18907a121baa460428a6040518080602001848152602001806020018381038352600c8152602001807f46697265205761726e696e670000000000000000000000000000000000000000815250602001838103825284818151815260200191508051906020019080838360005b838110156105e65780820151818401526020810190506105cb565b50505050905090810190601f1680156106135780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15b505050505050505050565b60015481565b826002819055508160038190555080600481905550505050565b6001600081548092919060010191905055506040518061014001604052808a815260200142815260200189815260200188815260200187815260200186815260200185815260200184815260200183815260200182815250600080600154815260200190815260200160002060008201518160000190805190602001906106d692919061074d565b506020820151816001015560408201518160020155606082015181600301556080820151816004015560a0820151816005015560c0820151816006015560e0820151816007015561010082015181600801556101208201518160090155905050505050505050505050565b60035481565b60025481565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061078e57805160ff19168380011785556107bc565b828001600101855582156107bc579182015b828111156107bb5782518255916020019190600101906107a0565b5b5090506107c991906107cd565b5090565b6107ef91905b808211156107eb5760008160009055506001016107d3565b5090565b9056fea264697066735822122018d1975a79558cdea36cb9d6132327dd703c76fbb2b28002f8cf7e535886a5b064736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"6080604052601e6002556032600355600160045534801561001f57600080fd5b506108288061002f6000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063b04322ac1161005b578063b04322ac146101ca578063eb03c7c91461020c578063ed5c0fd114610318578063fb44d377146103365761007d565b806324432ebc14610082578063700e07ce146100a05780637599be5d146101ac575b600080fd5b61008a610354565b6040518082815260200191505060405180910390f35b6101aa60048036036101208110156100b757600080fd5b81019080803590602001906401000000008111156100d457600080fd5b8201836020820111156100e657600080fd5b8035906020019184600183028401116401000000008311171561010857600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919050505061035a565b005b6101b461062e565b6040518082815260200191505060405180910390f35b61020a600480360360608110156101e057600080fd5b81019080803590602001909291908035906020019092919080359060200190929190505050610634565b005b610316600480360361012081101561022357600080fd5b810190808035906020019064010000000081111561024057600080fd5b82018360208201111561025257600080fd5b8035906020019184600183028401116401000000008311171561027457600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919050505061064e565b005b610320610741565b6040518082815260200191505060405180910390f35b61033e610747565b6040518082815260200191505060405180910390f35b60045481565b61036b89898989898989898961064e565b600254881315610453577ffb5777f0c4f49b4cfa3f4ab45ad5d512cc979e23f681ef4bb18907a121baa460428a6040518080602001848152602001806020018381038352601d8152602001807f54656d70657261747572652065786365656473205468726573686f6c64000000815250602001838103825284818151815260200191508051906020019080838360005b838110156104165780820151818401526020810190506103fb565b50505050905090810190601f1680156104435780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15b60035487111561053b577ffb5777f0c4f49b4cfa3f4ab45ad5d512cc979e23f681ef4bb18907a121baa460428a604051808060200184815260200180602001838103835260188152602001807f48756d69646974792062656c6f77205468726573686f6c640000000000000000815250602001838103825284818151815260200191508051906020019080838360005b838110156104fe5780820151818401526020810190506104e3565b50505050905090810190601f16801561052b5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15b600454811115610623577ffb5777f0c4f49b4cfa3f4ab45ad5d512cc979e23f681ef4bb18907a121baa460428a6040518080602001848152602001806020018381038352600c8152602001807f46697265205761726e696e670000000000000000000000000000000000000000815250602001838103825284818151815260200191508051906020019080838360005b838110156105e65780820151818401526020810190506105cb565b50505050905090810190601f1680156106135780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15b505050505050505050565b60015481565b826002819055508160038190555080600481905550505050565b6001600081548092919060010191905055506040518061014001604052808a815260200142815260200189815260200188815260200187815260200186815260200185815260200184815260200183815260200182815250600080600154815260200190815260200160002060008201518160000190805190602001906106d692919061074d565b506020820151816001015560408201518160020155606082015181600301556080820151816004015560a0820151816005015560c0820151816006015560e0820151816007015561010082015181600801556101208201518160090155905050505050505050505050565b60035481565b60025481565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061078e57805160ff19168380011785556107bc565b828001600101855582156107bc579182015b828111156107bb5782518255916020019190600101906107a0565b5b5090506107c991906107cd565b5090565b6107ef91905b808211156107eb5760008160009055506001016107d3565b5090565b9056fea264697066735822122018d1975a79558cdea36cb9d6132327dd703c76fbb2b28002f8cf7e535886a5b064736f6c634300060a0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"methodSignatureAsString\":\"WarningTriggered(string,uint256,string)\",\"name\":\"WarningTriggered\",\"type\":\"event\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":null,\"inputs\":[{\"name\":\"WarningType\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"timestamp\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"greenhouseName\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"addAndCheckSensorData(string,int256,uint256,uint256,uint256,uint256,uint256,uint256,uint256)\",\"name\":\"addAndCheckSensorData\",\"type\":\"function\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"nonpayable\",\"inputs\":[{\"name\":\"greenhouseName\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"temp\",\"type\":\"int256\",\"internalType\":\"int256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"int256\"},{\"name\":\"hum\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"p\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"lux\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"soil\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"air\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"relay\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"fire\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"addSensorData(string,int256,uint256,uint256,uint256,uint256,uint256,uint256,uint256)\",\"name\":\"addSensorData\",\"type\":\"function\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"nonpayable\",\"inputs\":[{\"name\":\"greenhouseName\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"temp\",\"type\":\"int256\",\"internalType\":\"int256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"int256\"},{\"name\":\"hum\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"p\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"lux\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"soil\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"air\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"relay\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"fire\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"fireThreshold()\",\"name\":\"fireThreshold\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}]},{\"methodSignatureAsString\":\"humThreshold()\",\"name\":\"humThreshold\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}]},{\"methodSignatureAsString\":\"recordCounter()\",\"name\":\"recordCounter\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}]},{\"methodSignatureAsString\":\"setThreshold(int256,uint256,uint256)\",\"name\":\"setThreshold\",\"type\":\"function\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"nonpayable\",\"inputs\":[{\"name\":\"newTempThreshold\",\"type\":\"int256\",\"internalType\":\"int256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"int256\"},{\"name\":\"newHumThreshold\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"newFireThreshold\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"tempThreshold()\",\"name\":\"tempThreshold\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"int256\",\"internalType\":\"int256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"int256\"}]}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ADDANDCHECKSENSORDATA = "addAndCheckSensorData";

    public static final String FUNC_ADDSENSORDATA = "addSensorData";

    public static final String FUNC_FIRETHRESHOLD = "fireThreshold";

    public static final String FUNC_HUMTHRESHOLD = "humThreshold";

    public static final String FUNC_RECORDCOUNTER = "recordCounter";

    public static final String FUNC_SETTHRESHOLD = "setThreshold";

    public static final String FUNC_TEMPTHRESHOLD = "tempThreshold";

    public static final Event WARNINGTRIGGERED_EVENT = new Event("WarningTriggered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected SmartWarning(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public List<WarningTriggeredEventResponse> getWarningTriggeredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(WARNINGTRIGGERED_EVENT, transactionReceipt);
        ArrayList<WarningTriggeredEventResponse> responses = new ArrayList<WarningTriggeredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            WarningTriggeredEventResponse typedResponse = new WarningTriggeredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.WarningType = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.greenhouseName = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeWarningTriggeredEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(WARNINGTRIGGERED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,fromBlock,toBlock,otherTopics,callback);
    }

    public void subscribeWarningTriggeredEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(WARNINGTRIGGERED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,callback);
    }

    public TransactionReceipt addAndCheckSensorData(String greenhouseName, BigInteger temp, BigInteger hum, BigInteger p, BigInteger lux, BigInteger soil, BigInteger air, BigInteger relay, BigInteger fire) {
        final Function function = new Function(
                FUNC_ADDANDCHECKSENSORDATA, 
                Arrays.<Type>asList(new Utf8String(greenhouseName),
                new Int256(temp),
                new Uint256(hum),
                new Uint256(p),
                new Uint256(lux),
                new Uint256(soil),
                new Uint256(air),
                new Uint256(relay),
                new Uint256(fire)),
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] addAndCheckSensorData(String greenhouseName, BigInteger temp, BigInteger hum, BigInteger p, BigInteger lux, BigInteger soil, BigInteger air, BigInteger relay, BigInteger fire, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_ADDANDCHECKSENSORDATA, 
                Arrays.<Type>asList(new Utf8String(greenhouseName),
                new Int256(temp),
                new Uint256(hum),
                new Uint256(p),
                new Uint256(lux),
                new Uint256(soil),
                new Uint256(air),
                new Uint256(relay),
                new Uint256(fire)),
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAddAndCheckSensorData(String greenhouseName, BigInteger temp, BigInteger hum, BigInteger p, BigInteger lux, BigInteger soil, BigInteger air, BigInteger relay, BigInteger fire) {
        final Function function = new Function(
                FUNC_ADDANDCHECKSENSORDATA, 
                Arrays.<Type>asList(new Utf8String(greenhouseName),
                new Int256(temp),
                new Uint256(hum),
                new Uint256(p),
                new Uint256(lux),
                new Uint256(soil),
                new Uint256(air),
                new Uint256(relay),
                new Uint256(fire)),
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple9<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> getAddAndCheckSensorDataInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_ADDANDCHECKSENSORDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Int256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple9<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(

                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (BigInteger) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (BigInteger) results.get(7).getValue(), 
                (BigInteger) results.get(8).getValue()
                );
    }

    public TransactionReceipt addSensorData(String greenhouseName, BigInteger temp, BigInteger hum, BigInteger p, BigInteger lux, BigInteger soil, BigInteger air, BigInteger relay, BigInteger fire) {
        final Function function = new Function(
                FUNC_ADDSENSORDATA, 
                Arrays.<Type>asList(new Utf8String(greenhouseName),
                new Int256(temp),
                new Uint256(hum),
                new Uint256(p),
                new Uint256(lux),
                new Uint256(soil),
                new Uint256(air),
                new Uint256(relay),
                new Uint256(fire)),
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] addSensorData(String greenhouseName, BigInteger temp, BigInteger hum, BigInteger p, BigInteger lux, BigInteger soil, BigInteger air, BigInteger relay, BigInteger fire, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_ADDSENSORDATA, 
                Arrays.<Type>asList(new Utf8String(greenhouseName),
                new Int256(temp),
                new Uint256(hum),
                new Uint256(p),
                new Uint256(lux),
                new Uint256(soil),
                new Uint256(air),
                new Uint256(relay),
                new Uint256(fire)),
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAddSensorData(String greenhouseName, BigInteger temp, BigInteger hum, BigInteger p, BigInteger lux, BigInteger soil, BigInteger air, BigInteger relay, BigInteger fire) {
        final Function function = new Function(
                FUNC_ADDSENSORDATA, 
                Arrays.<Type>asList(new Utf8String(greenhouseName),
                new Int256(temp),
                new Uint256(hum),
                new Uint256(p),
                new Uint256(lux),
                new Uint256(soil),
                new Uint256(air),
                new Uint256(relay),
                new Uint256(fire)),
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple9<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> getAddSensorDataInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_ADDSENSORDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Int256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple9<String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(

                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (BigInteger) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (BigInteger) results.get(7).getValue(), 
                (BigInteger) results.get(8).getValue()
                );
    }

    public BigInteger fireThreshold() throws ContractException {
        final Function function = new Function(FUNC_FIRETHRESHOLD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger humThreshold() throws ContractException {
        final Function function = new Function(FUNC_HUMTHRESHOLD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger recordCounter() throws ContractException {
        final Function function = new Function(FUNC_RECORDCOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public TransactionReceipt setThreshold(BigInteger newTempThreshold, BigInteger newHumThreshold, BigInteger newFireThreshold) {
        final Function function = new Function(
                FUNC_SETTHRESHOLD, 
                Arrays.<Type>asList(new Int256(newTempThreshold),
                new Uint256(newHumThreshold),
                new Uint256(newFireThreshold)),
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] setThreshold(BigInteger newTempThreshold, BigInteger newHumThreshold, BigInteger newFireThreshold, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETTHRESHOLD, 
                Arrays.<Type>asList(new Int256(newTempThreshold),
                new Uint256(newHumThreshold),
                new Uint256(newFireThreshold)),
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetThreshold(BigInteger newTempThreshold, BigInteger newHumThreshold, BigInteger newFireThreshold) {
        final Function function = new Function(
                FUNC_SETTHRESHOLD, 
                Arrays.<Type>asList(new Int256(newTempThreshold),
                new Uint256(newHumThreshold),
                new Uint256(newFireThreshold)),
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, BigInteger> getSetThresholdInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETTHRESHOLD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue()
                );
    }

    public BigInteger tempThreshold() throws ContractException {
        final Function function = new Function(FUNC_TEMPTHRESHOLD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public static SmartWarning load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new SmartWarning(contractAddress, client, credential);
    }

    public static SmartWarning deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(SmartWarning.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public static class WarningTriggeredEventResponse {
        public TransactionReceipt.Logs log;

        public String WarningType;

        public BigInteger timestamp;

        public String greenhouseName;
    }
}
