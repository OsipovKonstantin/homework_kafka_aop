package ru.t1.java.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.dto.TransactionFullDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final KafkaTransactionProducer kafkaTransactionProducer;
    private final TransactionMapper transactionMapper;

    @Value("${t1.kafka.topic.t1_demo_client_transactions}")
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

    @PostMapping("/transaction/add")
    public void addTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        kafkaTransactionProducer.sendTo(topic, transactionDto);
    }

    @GetMapping("/transaction/{transactionId}")
    public TransactionFullDto getTransaction(@PathVariable Long transactionId) {
        return transactionMapper.toFullDto(transactionService.getTransaction(transactionId));
    }
}
