package com.walletservice.infrastructure.config.producer;

import com.walletservice.infrastructure.config.properties.ConfluentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfig {

    private final ConfluentProperties confluentProperties;

    @PostConstruct
    public void init() {
        log.info("Kafka Properties loaded: server={}, saslSsl={}, plain={}, auth={}",
                confluentProperties.getServer(),
                confluentProperties.getSaslSsl(),
                confluentProperties.getPlain(),
                confluentProperties.getKafkaAuth());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        try {
            log.info("Starting producerFactory creation...");
            var configs = new HashMap<String, Object>();
            configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, confluentProperties.getServer());
            configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, confluentProperties.getSaslSsl());

            if (!"PLAINTEXT".equalsIgnoreCase(confluentProperties.getSaslSsl()) &&
                    confluentProperties.getKafkaAuth() != null) {
                configs.put(SaslConfigs.SASL_MECHANISM, confluentProperties.getPlain());
                configs.put(SaslConfigs.SASL_JAAS_CONFIG, confluentProperties.getKafkaAuth());
            }

            log.info("Kafka Producer configs: {}", configs);
            return new DefaultKafkaProducerFactory<>(configs);
        } catch (Exception e) {
            log.error("Error creating ProducerFactory", e);
            throw e;
        }
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        log.info("KafkaTemplate bean created");
        return new KafkaTemplate<>(producerFactory());
    }
}
