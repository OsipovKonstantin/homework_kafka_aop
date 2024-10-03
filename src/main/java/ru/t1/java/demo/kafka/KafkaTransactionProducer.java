package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionProducer {
    private final KafkaTemplate<String, TransactionDto> transactionDtoTemplate;
    private final KafkaTemplate<String, Long> longTemplate;
    @Value("${t1.kafka.topic.transaction-registered}")
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

    public void sendTo(String topic, TransactionDto transactionDto) {
        try {
            transactionDtoTemplate.send(topic, transactionDto).get();
            transactionDtoTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
