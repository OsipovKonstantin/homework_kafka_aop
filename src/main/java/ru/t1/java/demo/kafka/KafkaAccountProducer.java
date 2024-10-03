package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountProducer {
    private final KafkaTemplate<String, AccountDto> accountDtoTemplate;
    private final KafkaTemplate<String, Long> longTemplate;
    @Value("${t1.kafka.topic.account-registered}")
    private String topic;

    public void send(Long id) {
        try {
            longTemplate.setDefaultTopic(topic);
            longTemplate.sendDefault(UUID.randomUUID().toString(), id).get();
            longTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void sendTo(String topic, AccountDto accountDto) {
        try {
            accountDtoTemplate.send(topic, accountDto).get();
            accountDtoTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}