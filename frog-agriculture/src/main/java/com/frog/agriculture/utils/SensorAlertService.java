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
public class SensorAlertService extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50610c70806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063a4a7d8d31461003b578063f7541bfb14610073575b600080fd5b6100556004803603810190610050919061083b565b61008f565b60405161006a99989796959493929190610a8c565b60405180910390f35b61008d60048036038101906100889190610864565b61059d565b005b6000602052806000526040600020600091509050806000015490806001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101415780601f1061011657610100808354040283529160200191610141565b820191906000526020600020905b81548152906001019060200180831161012457829003601f168201915b505050505090806002018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101df5780601f106101b4576101008083540402835291602001916101df565b820191906000526020600020905b8154815290600101906020018083116101c257829003601f168201915b505050505090806003018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561027d5780601f106102525761010080835404028352916020019161027d565b820191906000526020600020905b81548152906001019060200180831161026057829003601f168201915b505050505090806004018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561031b5780601f106102f05761010080835404028352916020019161031b565b820191906000526020600020905b8154815290600101906020018083116102fe57829003601f168201915b505050505090806005018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103b95780601f1061038e576101008083540402835291602001916103b9565b820191906000526020600020905b81548152906001019060200180831161039c57829003601f168201915b505050505090806006018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104575780601f1061042c57610100808354040283529160200191610457565b820191906000526020600020905b81548152906001019060200180831161043a57829003601f168201915b505050505090806007018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104f55780601f106104ca576101008083540402835291602001916104f5565b820191906000526020600020905b8154815290600101906020018083116104d857829003601f168201915b505050505090806008018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105935780601f1061056857610100808354040283529160200191610593565b820191906000526020600020905b81548152906001019060200180831161057657829003601f168201915b5050505050905089565b6040518061012001604052808a8152602001898152602001888152602001878152602001868152602001858152602001848152602001838152602001828152506000808b815260200190815260200160002060008201518160000155602082015181600101908051906020019061061592919061072d565b50604082015181600201908051906020019061063292919061072d565b50606082015181600301908051906020019061064f92919061072d565b50608082015181600401908051906020019061066c92919061072d565b5060a082015181600501908051906020019061068992919061072d565b5060c08201518160060190805190602001906106a692919061072d565b5060e08201518160070190805190602001906106c392919061072d565b506101008201518160080190805190602001906106e192919061072d565b509050507f2f565c4bfae959d97682a05271881135b7ecf76c743af0223d5832bed8ea07188987838560405161071a9493929190610a32565b60405180910390a1505050505050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061076e57805160ff191683800117855561079c565b8280016001018555821561079c579182015b8281111561079b578251825591602001919060010190610780565b5b5090506107a991906107ad565b5090565b6107cf91905b808211156107cb5760008160009055506001016107b3565b5090565b90565b600082601f8301126107e357600080fd5b81356107f66107f182610b7e565b610b51565b9150808252602083016020830185838301111561081257600080fd5b61081d838284610bd0565b50505092915050565b60008135905061083581610c23565b92915050565b60006020828403121561084d57600080fd5b600061085b84828501610826565b91505092915050565b60008060008060008060008060006101208a8c03121561088357600080fd5b60006108918c828d01610826565b99505060208a013567ffffffffffffffff8111156108ae57600080fd5b6108ba8c828d016107d2565b98505060408a013567ffffffffffffffff8111156108d757600080fd5b6108e38c828d016107d2565b97505060608a013567ffffffffffffffff81111561090057600080fd5b61090c8c828d016107d2565b96505060808a013567ffffffffffffffff81111561092957600080fd5b6109358c828d016107d2565b95505060a08a013567ffffffffffffffff81111561095257600080fd5b61095e8c828d016107d2565b94505060c08a013567ffffffffffffffff81111561097b57600080fd5b6109878c828d016107d2565b93505060e08a013567ffffffffffffffff8111156109a457600080fd5b6109b08c828d016107d2565b9250506101008a013567ffffffffffffffff8111156109ce57600080fd5b6109da8c828d016107d2565b9150509295985092959850929598565b60006109f582610baa565b6109ff8185610bb5565b9350610a0f818560208601610bdf565b610a1881610c12565b840191505092915050565b610a2c81610bc6565b82525050565b6000608082019050610a476000830187610a23565b8181036020830152610a5981866109ea565b90508181036040830152610a6d81856109ea565b90508181036060830152610a8181846109ea565b905095945050505050565b600061012082019050610aa2600083018c610a23565b8181036020830152610ab4818b6109ea565b90508181036040830152610ac8818a6109ea565b90508181036060830152610adc81896109ea565b90508181036080830152610af081886109ea565b905081810360a0830152610b0481876109ea565b905081810360c0830152610b1881866109ea565b905081810360e0830152610b2c81856109ea565b9050818103610100830152610b4181846109ea565b90509a9950505050505050505050565b6000604051905081810181811067ffffffffffffffff82111715610b7457600080fd5b8060405250919050565b600067ffffffffffffffff821115610b9557600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b83811015610bfd578082015181840152602081019050610be2565b83811115610c0c576000848401525b50505050565b6000601f19601f8301169050919050565b610c2c81610bc6565b8114610c3757600080fd5b5056fea2646970667358221220106c277887d731724dad379204cc624edf0ec737ba889e2b06a410b2a6ae050c64736f6c634300060a0033"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50610c70806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063a4a7d8d31461003b578063f7541bfb14610073575b600080fd5b6100556004803603810190610050919061083b565b61008f565b60405161006a99989796959493929190610a8c565b60405180910390f35b61008d60048036038101906100889190610864565b61059d565b005b6000602052806000526040600020600091509050806000015490806001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101415780601f1061011657610100808354040283529160200191610141565b820191906000526020600020905b81548152906001019060200180831161012457829003601f168201915b505050505090806002018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101df5780601f106101b4576101008083540402835291602001916101df565b820191906000526020600020905b8154815290600101906020018083116101c257829003601f168201915b505050505090806003018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561027d5780601f106102525761010080835404028352916020019161027d565b820191906000526020600020905b81548152906001019060200180831161026057829003601f168201915b505050505090806004018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561031b5780601f106102f05761010080835404028352916020019161031b565b820191906000526020600020905b8154815290600101906020018083116102fe57829003601f168201915b505050505090806005018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103b95780601f1061038e576101008083540402835291602001916103b9565b820191906000526020600020905b81548152906001019060200180831161039c57829003601f168201915b505050505090806006018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104575780601f1061042c57610100808354040283529160200191610457565b820191906000526020600020905b81548152906001019060200180831161043a57829003601f168201915b505050505090806007018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104f55780601f106104ca576101008083540402835291602001916104f5565b820191906000526020600020905b8154815290600101906020018083116104d857829003601f168201915b505050505090806008018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105935780601f1061056857610100808354040283529160200191610593565b820191906000526020600020905b81548152906001019060200180831161057657829003601f168201915b5050505050905089565b6040518061012001604052808a8152602001898152602001888152602001878152602001868152602001858152602001848152602001838152602001828152506000808b815260200190815260200160002060008201518160000155602082015181600101908051906020019061061592919061072d565b50604082015181600201908051906020019061063292919061072d565b50606082015181600301908051906020019061064f92919061072d565b50608082015181600401908051906020019061066c92919061072d565b5060a082015181600501908051906020019061068992919061072d565b5060c08201518160060190805190602001906106a692919061072d565b5060e08201518160070190805190602001906106c392919061072d565b506101008201518160080190805190602001906106e192919061072d565b509050507f2f565c4bfae959d97682a05271881135b7ecf76c743af0223d5832bed8ea07188987838560405161071a9493929190610a32565b60405180910390a1505050505050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061076e57805160ff191683800117855561079c565b8280016001018555821561079c579182015b8281111561079b578251825591602001919060010190610780565b5b5090506107a991906107ad565b5090565b6107cf91905b808211156107cb5760008160009055506001016107b3565b5090565b90565b600082601f8301126107e357600080fd5b81356107f66107f182610b7e565b610b51565b9150808252602083016020830185838301111561081257600080fd5b61081d838284610bd0565b50505092915050565b60008135905061083581610c23565b92915050565b60006020828403121561084d57600080fd5b600061085b84828501610826565b91505092915050565b60008060008060008060008060006101208a8c03121561088357600080fd5b60006108918c828d01610826565b99505060208a013567ffffffffffffffff8111156108ae57600080fd5b6108ba8c828d016107d2565b98505060408a013567ffffffffffffffff8111156108d757600080fd5b6108e38c828d016107d2565b97505060608a013567ffffffffffffffff81111561090057600080fd5b61090c8c828d016107d2565b96505060808a013567ffffffffffffffff81111561092957600080fd5b6109358c828d016107d2565b95505060a08a013567ffffffffffffffff81111561095257600080fd5b61095e8c828d016107d2565b94505060c08a013567ffffffffffffffff81111561097b57600080fd5b6109878c828d016107d2565b93505060e08a013567ffffffffffffffff8111156109a457600080fd5b6109b08c828d016107d2565b9250506101008a013567ffffffffffffffff8111156109ce57600080fd5b6109da8c828d016107d2565b9150509295985092959850929598565b60006109f582610baa565b6109ff8185610bb5565b9350610a0f818560208601610bdf565b610a1881610c12565b840191505092915050565b610a2c81610bc6565b82525050565b6000608082019050610a476000830187610a23565b8181036020830152610a5981866109ea565b90508181036040830152610a6d81856109ea565b90508181036060830152610a8181846109ea565b905095945050505050565b600061012082019050610aa2600083018c610a23565b8181036020830152610ab4818b6109ea565b90508181036040830152610ac8818a6109ea565b90508181036060830152610adc81896109ea565b90508181036080830152610af081886109ea565b905081810360a0830152610b0481876109ea565b905081810360c0830152610b1881866109ea565b905081810360e0830152610b2c81856109ea565b9050818103610100830152610b4181846109ea565b90509a9950505050505050505050565b6000604051905081810181811067ffffffffffffffff82111715610b7457600080fd5b8060405250919050565b600067ffffffffffffffff821115610b9557600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b83811015610bfd578082015181840152602081019050610be2565b83811115610c0c576000848401525b50505050565b6000601f19601f8301169050919050565b610c2c81610bc6565b8114610c3757600080fd5b5056fea2646970667358221220106c277887d731724dad379204cc624edf0ec737ba889e2b06a410b2a6ae050c64736f6c634300060a0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"methodSignatureAsString\":\"SensorAlertTriggered(uint256,string,string,string)\",\"name\":\"SensorAlertTriggered\",\"type\":\"event\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":null,\"inputs\":[{\"name\":\"id\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"paramName\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertLevel\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertTime\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}],\"outputs\":[]},{\"methodSignatureAsString\":\"SensorAlertDatas(uint256)\",\"name\":\"SensorAlertDatas\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"inputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"outputs\":[{\"name\":\"id\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"alertType\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertMessage\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"paramName\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"paramValue\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"thresholdMin\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"thresholdMax\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertTime\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"alertLevel\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}]},{\"methodSignatureAsString\":\"addSensorAlertData(uint256,string,string,string,string,string,string,string,string)\",\"name\":\"addSensorAlertData\",\"type\":\"function\",\"constant\":false,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"nonpayable\",\"inputs\":[{\"name\":\"_id\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"},{\"name\":\"_alertType\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertMessage\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_paramName\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_paramValue\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_thresholdMin\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_thresholdMax\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertTime\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"},{\"name\":\"_alertLevel\",\"type\":\"string\",\"internalType\":\"string\",\"indexed\":false,\"components\":[],\"dynamic\":true,\"typeAsString\":\"string\"}],\"outputs\":[]}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_SENSORALERTDATAS = "SensorAlertDatas";

    public static final String FUNC_ADDSENSORALERTDATA = "addSensorAlertData";

    public static final Event SENSORALERTTRIGGERED_EVENT = new Event("SensorAlertTriggered",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected SensorAlertService(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public List<SensorAlertTriggeredEventResponse> getSensorAlertTriggeredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(SENSORALERTTRIGGERED_EVENT, transactionReceipt);
        ArrayList<SensorAlertTriggeredEventResponse> responses = new ArrayList<SensorAlertTriggeredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            SensorAlertTriggeredEventResponse typedResponse = new SensorAlertTriggeredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.paramName = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.alertLevel = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.alertTime = (String) eventValues.getNonIndexedValues().get(3).getValue();
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

    public Tuple9<BigInteger, String, String, String, String, String, String, String, String> SensorAlertDatas(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_SENSORALERTDATAS,
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple9<BigInteger, String, String, String, String, String, String, String, String>(
                (BigInteger) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue(),
                (String) results.get(4).getValue(),
                (String) results.get(5).getValue(),
                (String) results.get(6).getValue(),
                (String) results.get(7).getValue(),
                (String) results.get(8).getValue());
    }

    public TransactionReceipt addSensorAlertData(BigInteger _id, String _alertType, String _alertMessage, String _paramName, String _paramValue, String _thresholdMin, String _thresholdMax, String _alertTime, String _alertLevel) {
        final Function function = new Function(
                FUNC_ADDSENSORALERTDATA,
                Arrays.<Type>asList(new Uint256(_id),
                new Utf8String(_alertType),
                new Utf8String(_alertMessage),
                new Utf8String(_paramName),
                new Utf8String(_paramValue),
                new Utf8String(_thresholdMin),
                new Utf8String(_thresholdMax),
                new Utf8String(_alertTime),
                new Utf8String(_alertLevel)),
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] addSensorAlertData(BigInteger _id, String _alertType, String _alertMessage, String _paramName, String _paramValue, String _thresholdMin, String _thresholdMax, String _alertTime, String _alertLevel, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_ADDSENSORALERTDATA,
                Arrays.<Type>asList(new Uint256(_id),
                new Utf8String(_alertType),
                new Utf8String(_alertMessage),
                new Utf8String(_paramName),
                new Utf8String(_paramValue),
                new Utf8String(_thresholdMin),
                new Utf8String(_thresholdMax),
                new Utf8String(_alertTime),
                new Utf8String(_alertLevel)),
                Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAddSensorAlertData(BigInteger _id, String _alertType, String _alertMessage, String _paramName, String _paramValue, String _thresholdMin, String _thresholdMax, String _alertTime, String _alertLevel) {
        final Function function = new Function(
                FUNC_ADDSENSORALERTDATA,
                Arrays.<Type>asList(new Uint256(_id),
                new Utf8String(_alertType),
                new Utf8String(_alertMessage),
                new Utf8String(_paramName),
                new Utf8String(_paramValue),
                new Utf8String(_thresholdMin),
                new Utf8String(_thresholdMax),
                new Utf8String(_alertTime),
                new Utf8String(_alertLevel)),
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple9<BigInteger, String, String, String, String, String, String, String, String> getAddSensorAlertDataInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_ADDSENSORALERTDATA,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple9<BigInteger, String, String, String, String, String, String, String, String>(

                (BigInteger) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue(),
                (String) results.get(4).getValue(),
                (String) results.get(5).getValue(),
                (String) results.get(6).getValue(),
                (String) results.get(7).getValue(),
                (String) results.get(8).getValue()
                );
    }

    public static SensorAlertService load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new SensorAlertService(contractAddress, client, credential);
    }

    public static SensorAlertService deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(SensorAlertService.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public static class SensorAlertTriggeredEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger id;

        public String paramName;

        public String alertLevel;

        public String alertTime;
    }
}
