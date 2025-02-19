package com.frog.IaAgriculture.config;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.animals.ContractInfo;
import vip.blockchain.fishsheService.FishTraceabFrameService;

import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class SdkBeanConfig {


    @Autowired
    private SystemConfig config;
    @Value("${system.contract.insurePlatformAddress}")
    private String PlatformAddress;
    @Value("${system.contract.insureFishTraceabAddress}")
    private String fishTraceabAddress;
    @Bean
    public Client client() throws Exception {
        String certPaths = this.config.getCertPath();
        String[] possibilities = certPaths.split(",|;");
        for(String certPath: possibilities ) {
            try{
                ConfigProperty property = new ConfigProperty();


                configNetwork(property);
                configCryptoMaterial(property,certPath);

                ConfigOption configOption = new ConfigOption(property);

                Client client = new BcosSDK(configOption).getClient(config.getGroupId());
                BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
                log.info("Chain connect successful. Current block number {}", blockNumber);
                CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
                CryptoKeyPair keyPair = cryptoSuite.createKeyPair(config.getPrivateKey());
                client.getCryptoSuite().setCryptoKeyPair(keyPair);
                return client;
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
                try{
                    Thread.sleep(5000);
                }catch (Exception e) {}
            }
        }
        throw new ConfigException("Failed to connect to peers:" + config.getPeers());
    }

    public void configNetwork(ConfigProperty configProperty) {
        String peerStr = config.getPeers();
        List<String> peers = Arrays.stream(peerStr.split(",")).collect(Collectors.toList());
        Map<String, Object> networkConfig = new HashMap<>();
        networkConfig.put("peers", peers);

        configProperty.setNetwork(networkConfig);
    }



    public void configCryptoMaterial(ConfigProperty configProperty,String certPath) {
        Map<String, Object> cryptoMaterials = new HashMap<>();
        cryptoMaterials.put("certPath", certPath);
        configProperty.setCryptoMaterial(cryptoMaterials);
    }
    @Bean(name = "fishTraceabContractInfo")
    public ContractInfo contractInfo(){
        ClassLoader classLoader = getClass().getClassLoader();
        URL abiUrl = classLoader.getResource("abi");
        if (abiUrl != null) {
            return new ContractInfo(fishTraceabAddress,abiUrl.getPath()+"/","","FishTraceabFrame.abi");
        }
        return null;
    }
    @Bean
    public PlatformService platformService(Client client) {
        return new PlatformService(PlatformAddress, client, client.getCryptoSuite().getCryptoKeyPair());
    }
    @Bean
    public FishTraceabFrameService fishTraceabFrameService(Client client, ContractInfo fishTraceabContractInfo){

        return new FishTraceabFrameService(fishTraceabContractInfo, client, client.getCryptoSuite().getCryptoKeyPair());
    }

}
