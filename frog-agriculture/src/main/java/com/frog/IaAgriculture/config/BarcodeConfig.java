package com.frog.IaAgriculture.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(
        prefix = "barcode"
)
public class BarcodeConfig {

    private String path;

}
