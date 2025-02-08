package com.frog.IaAgriculture.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(
    prefix = "system"
)
public class SystemConfig {
  private String peers;

  private int groupId = 1;

  private String certPath;

  private String privateKey;

}
