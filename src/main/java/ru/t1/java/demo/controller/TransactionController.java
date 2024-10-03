package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final KafkaTransactionProducer kafkaTransactionProducer;

    @Value("${t1.kafka.topic.transaction}")
    private String topic;

    @LogException
    @Track
    @GetMapping(value = "/loadTransactions")
    @HandlingResult
    public void parseSource() {
        List<TransactionDto> transactionDtos = transactionService.parseJson();
        transactionDtos.forEach(dto -> {
            kafkaTransactionProducer.sendTo(topic, dto);
        });
    }

}