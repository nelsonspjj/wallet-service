package com.walletservice.infrastructure.config.producer;

import com.walletservice.infrastructure.config.properties.ConfluentProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

    private final ConfluentProperties confluentProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        var configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, confluentProperties.getServer());
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, confluentProperties.getSaslSsl());
        configs.put(SaslConfigs.SASL_MECHANISM, confluentProperties.getPlain());
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, confluentProperties.getKafkaAuth());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createTopicSubscriptionProduct() {
        return new NewTopic(confluentProperties.getTopicNameWalletTransactions(), confluentProperties.getTopicPartitions(), (short) 1);
    }
}
