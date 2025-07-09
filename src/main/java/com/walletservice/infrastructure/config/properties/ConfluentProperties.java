package com.walletservice.infrastructure.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "confluent.kafka")
public class ConfluentProperties {

    private String server;
    private String username;
    private String password;
    private String topicNameWalletTransactions;
    private String saslSsl;
    private String plain;
    private int topicPartitions;

    public String getKafkaAuth() {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        return String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                username, password
        );
    }
}
