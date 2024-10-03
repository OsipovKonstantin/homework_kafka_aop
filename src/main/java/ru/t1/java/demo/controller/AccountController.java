package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.KafkaAccountProducer;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final KafkaAccountProducer kafkaAccountProducer;
    @Value("${t1.kafka.topic.account}")
    private String topic;

    @LogException
    @Track
    @GetMapping(value = "/loadAccounts")
    @HandlingResult
    public void parseSource() {
        List<AccountDto> accountDtos = accountService.parseJson();
        accountDtos.forEach(dto -> {
            kafkaAccountProducer.sendTo(topic, dto);
        });
    }

}