package com.walletservice.infrastructure.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms.auth")
public class AuthServiceProperties {
    private String jwtSecret;
    private String kafkaHost;
}