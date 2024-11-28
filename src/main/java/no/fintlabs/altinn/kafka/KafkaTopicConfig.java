package no.fintlabs.altinn.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic createAltinnInstancesTopic(){
        log.info("Creating Kafka topic: altinn-instances");
        return new NewTopic("altinn-instances", 1, (short) 1);
    }
}