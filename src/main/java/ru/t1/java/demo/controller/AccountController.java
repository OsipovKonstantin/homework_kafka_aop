package ru.t1.java.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.KafkaAccountProducer;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountCreateDto;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.AccountFullDto;
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
    private final AccountMapper accountMapper;

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

    @PostMapping("/account/register")
    public AccountFullDto registerAccount(@Valid @RequestBody AccountCreateDto accountCreateDto) {
        Account account = accountService.registerAccount(accountMapper.toEntity(accountCreateDto));
        return accountMapper.toFullDto(account);
    }

    @GetMapping("/account/{accountId}")
    public AccountFullDto getAccountById(@PathVariable Long accountId) {
        return accountMapper.toFullDto(accountService.getAccountById(accountId));
    }

    @PutMapping("/account/block-debit/{accountId}")
    public AccountFullDto blockDebitAccount(@PathVariable Long accountId) {
        return accountMapper.toFullDto(accountService.blockDebitAccount(accountId));
    }
}
