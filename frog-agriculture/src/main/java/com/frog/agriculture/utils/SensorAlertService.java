package com.frog.agriculture.utils;

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
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple7;
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
public class SensorAlertService extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50610b7c806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80637599be5d14610046578063a4a7d8d314610064578063c916e5d41461009a575b600080fd5b61004e6100b6565b60405161005b9190610a42565b60405180910390f35b61007e600480360381019061007991906108e5565b6100bc565b60405161009197969594939291906109a2565b60405180910390f35b6100b460048036038101906100af919061079f565b610526565b005b60015481565b6000602052806000526040600020600091509050806000018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101685780601f1061013d57610100808354040283529160200191610168565b820191906000526020600020905b81548152906001019060200180831161014b57829003601f168201915b505050505090806001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102065780601f106101db57610100808354040283529160200191610206565b820191906000526020600020905b8154815290600101906020018083116101e957829003601f168201915b505050505090806002018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102a45780601f10610279576101008083540402835291602001916102a4565b820191906000526020600020905b81548152906001019060200180831161028757829003601f168201915b505050505090806003018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103425780601f1061031757610100808354040283529160200191610342565b820191906000526020600020905b81548152906001019060200180831161032557829003601f168201915b505050505090806004018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103e05780601f106103b5576101008083540402835291602001916103e0565b820191906000526020600020905b8154815290600101906020018083116103c357829003601f168201915b505050505090806005018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561047e5780601f106104535761010080835404028352916020019161047e565b820191906000526020600020905b81548152906001019060200180831161046157829003601f168201915b505050505090806006018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561051c5780601f106104f15761010080835404028352916020019161051c565b820191906000526020600020905b8154815290600101906020018083116104ff57829003601f168201915b5050505050905087565b6040518060e001604052808881526020018781526020018681526020018581526020018481526020018381526020018281525060008060015481526020019081526020016000206000820151816000019080519060200190610589929190610691565b5060208201518160010190805190602001906105a6929190610691565b5060408201518160020190805190602001906105c3929190610691565b5060608201518160030190805190602001906105e0929190610691565b5060808201518160040190805190602001906105fd929190610691565b5060a082015181600501908051906020019061061a929190610691565b5060c0820151816006019080519060200190610637929190610691565b509050506001600081548092919060010191905055507f5c2c91392530690cc0e742f59cc9ad76f0b350551a18ff132152d09e2ebbd5bd87838360405161068093929190610956565b60405180910390a150505050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106106d257805160ff1916838001178555610700565b82800160010185558215610700579182015b828111156106ff5782518255916020019190600101906106e4565b5b50905061070d9190610711565b5090565b61073391905b8082111561072f576000816000905550600101610717565b5090565b90565b600082601f83011261074757600080fd5b813561075a61075582610a8a565b610a5d565b9150808252602083016020830185838301111561077657600080fd5b610781838284610adc565b50505092915050565b60008135905061079981610b2f565b92915050565b600080600080600080600060e0888a0312156107ba57600080fd5b600088013567ffffffffffffffff8111156107d457600080fd5b6107e08a828b01610736565b975050602088013567ffffffffffffffff8111156107fd57600080fd5b6108098a828b01610736565b965050604088013567ffffffffffffffff81111561082657600080fd5b6108328a828b01610736565b955050606088013567ffffffffffffffff81111561084f57600080fd5b61085b8a828b01610736565b945050608088013567ffffffffffffffff81111561087857600080fd5b6108848a828b01610736565b93505060a088013567ffffffffffffffff8111156108a157600080fd5b6108ad8a828b01610736565b92505060c088013567ffffffffffffffff8111156108ca57600080fd5b6108d68a828b01610736565b91505092959891949750929550565b6000602082840312156108f757600080fd5b60006109058482850161078a565b91505092915050565b600061091982610ab6565b6109238185610ac1565b9350610933818560208601610aeb565b61093c81610b1e565b840191505092915050565b61095081610ad2565b82525050565b60006060820190508181036000830152610970818661090e565b90508181036020830152610984818561090e565b90508181036040830152610998818461090e565b9050949350505050565b600060e08201905081810360008301526109bc818a61090e565b905081810360208301526109d0818961090e565b905081810360408301526109e4818861090e565b905081810360608301526109f8818761090e565b90508181036080830152610a0c818661090e565b905081810360a0830152610a20818561090e565b905081810360c0830152610a34818461090e565b905098975050505050505050565b6000602082019050610a576000830184610947565b92915050565b6000604051905081810181811067ffffffffffffffff82111715610a8057600080fd5b8060405250919050565b600067ffffffffffffffff821115610aa157600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b83811015610b09578082015181840152602081019050610aee565b83811115610b18576000848401525b50505050565b6000601f19601f8301169050919050565b610b3881610ad2565b8114610b4357600080fd5b5056fea2646970667358221220e0f60e4019f0951a132074922a2503d24767c14a684bc9db33b01f423e85529d64736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50610b7c806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80637599be5d14610046578063a4a7d8d314610064578063c916e5d41461009a575b600080fd5b61004e6100b6565b60405161005b9190610a42565b60405180910390f35b61007e600480360381019061007991906108e5565b6100bc565b60405161009197969594939291906109a2565b60405180910390f35b6100b460048036038101906100af919061079f565b610526565b005b60015481565b6000602052806000526040600020600091509050806000018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101685780601f1061013d57610100808354040283529160200191610168565b820191906000526020600020905b81548152906001019060200180831161014b57829003601f168201915b505050505090806001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102065780601f106101db57610100808354040283529160200191610206565b820191906000526020600020905b8154815290600101906020018083116101e957829003601f168201915b505050505090806002018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102a45780601f10610279576101008083540402835291602001916102a4565b820191906000526020600020905b81548152906001019060200180831161028757829003601f168201915b505050505090806003018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103425780601f1061031757610100808354040283529160200191610342565b820191906000526020600020905b81548152906001019060200180831161032557829003601f168201915b505050505090806004018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103e05780601f106103b5576101008083540402835291602001916103e0565b820191906000526020600020905b8154815290600101906020018083116103c357829003601f168201915b505050505090806005018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561047e5780601f106104535761010080835404028352916020019161047e565b820191906000526020600020905b81548152906001019060200180831161046157829003601f168201915b505050505090806006018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561051c5780601f106104f15761010080835404028352916020019161051c565b820191906000526020600020905b8154815290600101906020018083116104ff57829003601f168201915b5050505050905087565b6040518060e001604052808881526020018781526020018681526020018581526020018481526020018381526020018281525060008060015481526020019081526020016000206000820151816000019080519060200190610589929190610691565b5060208201518160010190805190602001906105a6929190610691565b5060408201518160020190805190602001906105c3929190610691565b5060608201518160030190805190602001906105e0929190610691565b5060808201518160040190805190602001906105fd929190610691565b5060a082015181600501908051906020019061061a929190610691565b5060c0820151816006019080519060200190610637929190610691565b509050506001600081548092919060010191905055507f5c2c91392530690cc0e742f59cc9ad76f0b350551a18ff132152d09e2ebbd5bd87838360405161068093929190610956565b60405180910390a150505050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106106d257805160ff1916838001178555610700565b82800160010185558215610700579182015b828111156106ff5782518255916020019190600101906106e4565b5b50905061070d9190610711565b5090565b61073391905b8082111561072f576000816000905550600101610717565b5090565b90565b600082601f83011261074757600080fd5b813561075a61075582610a8a565b610a5d565b9150808252602083016020830185838301111561077657600080fd5b610781838284610adc565b50505092915050565b60008135905061079981610b2f565b92915050565b600080600080600080600060e0888a0312156107ba57600080fd5b600088013567ffffffffffffffff8111156107d457600080fd5b6107e08a828b01610736565b975050602088013567ffffffffffffffff8111156107fd57600080fd5b6108098a828b01610736565b965050604088013567ffffffffffffffff81111561082657600080fd5b6108328a828b01610736565b955050606088013567ffffffffffffffff81111561084f57600080fd5b61085b8a828b01610736565b945050608088013567ffffffffffffffff81111561087857600080fd5b6108848a828b01610736565b93505060a088013567ffffffffffffffff8111156108a157600080fd5b6108ad8a828b01610736565b92505060c088013567ffffffffffffffff8111156108ca57600080fd5b6108d68a828b01610736565b91505092959891949750929550565b6000602082840312156108f757600080fd5b60006109058482850161078a565b91505092915050565b600061091982610ab6565b6109238185610ac1565b9350610933818560208601610aeb565b61093c81610b1e565b840191505092915050565b61095081610ad2565b82525050565b60006060820190508181036000830152610970818661090e565b90508181036020830152610984818561090e565b90508181036040830152610998818461090e565b9050949350505050565b600060e08201905081810360008301526109bc818a61090e565b905081810360208301526109d0818961090e565b905081810360408301526109e4818861090e565b905081810360608301526109f8818761090e565b90508181036080830152610a0c818661090e565b905081810360a0830152610a20818561090e565b905081810360c0830152610a34818461090e565b905098975050505050505050565b6000602082019050610a576000830184610947565b92915050565b6000604051905081810181811067ffffffffffffffff82111715610a8057600080fd5b8060405250919050565b600067ffffffffffffffff821115610aa157600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b83811015610b09578082015181840152602081019050610aee565b83811115610b18576000848401525b50505050565b6000601f19601f8301169050919050565b610b3881610ad2565b8114610b4357600080fd5b5056fea2646970667358221220e0f60e4019f0951a132074922a2503d24767c14a684bc9db33b01f423e85529d64736f6c634300060a0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"methodSignatureAsString\":\"SensorAlertTriggered(string,string,string)\",\"name\":\"SensorAlertTriggered\",\"type\":\"event\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":null,\"inputs\":[{\"name\":\"batchId\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertLevel\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertTime\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"SensorAlertDatas(uint256)\",\"name\":\"SensorAlertDatas\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"outputs\":[{\"name\":\"batchId\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertType\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertMessage\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"thresholdMax\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"thresholdMin\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertLevel\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertTime\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}]},{\"methodSignatureAsString\":\"addSensorAlertData(string,string,string,string,string,string,string)\",\"name\":\"addSensorAlertData\",\"type\":\"function\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"nonpayable\",\"inputs\":[{\"name\":\"_batchId\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertType\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertMessage\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_thresholdMax\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_thresholdMin\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertLevel\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertTime\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"recordCounter()\",\"name\":\"recordCounter\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}]}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_SENSORALERTDATAS = "SensorAlertDatas";

    public static final String FUNC_ADDSENSORALERTDATA = "addSensorAlertData";

    public static final String FUNC_RECORDCOUNTER = "recordCounter";

    public static final Event SENSORALERTTRIGGERED_EVENT = new Event("SensorAlertTriggered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected SensorAlertService(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public List<SensorAlertTriggeredEventResponse> getSensorAlertTriggeredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SENSORALERTTRIGGERED_EVENT, transactionReceipt);
        ArrayList<SensorAlertTriggeredEventResponse> responses = new ArrayList<SensorAlertTriggeredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SensorAlertTriggeredEventResponse typedResponse = new SensorAlertTriggeredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.batchId = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.alertLevel = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.alertTime = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeSensorAlertTriggeredEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(SENSORALERTTRIGGERED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,fromBlock,toBlock,otherTopics,callback);
    }

    public void subscribeSensorAlertTriggeredEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(SENSORALERTTRIGGERED_EVENT);
        subscribeEvent(ABI,BINARY,topic0,callback);
    }

    public Tuple7<String, String, String, String, String, String, String> SensorAlertDatas(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_SENSORALERTDATAS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple7<String, String, String, String, String, String, String>(
                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (String) results.get(5).getValue(), 
                (String) results.get(6).getValue());
    }

    public TransactionReceipt addSensorAlertData(String _batchId, String _alertType, String _alertMessage, String _thresholdMax, String _thresholdMin, String _alertLevel, String _alertTime) {
        final Function function = new Function(
                FUNC_ADDSENSORALERTDATA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_batchId), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertType), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertMessage), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_thresholdMax), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_thresholdMin), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertLevel), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] addSensorAlertData(String _batchId, String _alertType, String _alertMessage, String _thresholdMax, String _thresholdMin, String _alertLevel, String _alertTime, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_ADDSENSORALERTDATA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_batchId), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertType), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertMessage), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_thresholdMax), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_thresholdMin), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertLevel), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertTime)), 
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAddSensorAlertData(String _batchId, String _alertType, String _alertMessage, String _thresholdMax, String _thresholdMin, String _alertLevel, String _alertTime) {
        final Function function = new Function(
                FUNC_ADDSENSORALERTDATA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_batchId), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertType), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertMessage), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_thresholdMax), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_thresholdMin), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertLevel), 
                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(_alertTime)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple7<String, String, String, String, String, String, String> getAddSensorAlertDataInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_ADDSENSORALERTDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple7<String, String, String, String, String, String, String>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (String) results.get(5).getValue(), 
                (String) results.get(6).getValue()
                );
    }

    public BigInteger recordCounter() throws ContractException {
        final Function function = new Function(FUNC_RECORDCOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public static SensorAlertService load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new SensorAlertService(contractAddress, client, credential);
    }

    public static SensorAlertService deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(SensorAlertService.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public static class SensorAlertTriggeredEventResponse {
        public TransactionReceipt.Logs log;

        public String batchId;

        public String alertLevel;

        public String alertTime;
    }
}
