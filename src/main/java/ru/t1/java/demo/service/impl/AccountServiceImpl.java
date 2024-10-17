package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.exception.InvalidException;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.kafka.KafkaAccountProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountType;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final KafkaAccountProducer kafkaAccountProducer;

    @Metric
    @Override
    public void addAccount(List<Account> accounts) {
        accountRepository.saveAll(accounts)
                .stream()
                .map(account -> account.getId())
                .forEach(kafkaAccountProducer::send);
    }

    @Metric
    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accounts;
        try {
            accounts = mapper.readValue(new File("src/main/resources/MOCK_ACCOUNT_DATA.json"), AccountDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(accounts);
    }

    @Override
    public Account registerAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account getAccountById(Long accountId) {
        return findById(accountId);
    }

    @Override
    public Account blockDebitAccount(Long accountId) {
        Account account = findById(accountId);
        if (account.getType().equals(AccountType.CREDIT)) {
            throw new InvalidException(String.format("Кредитный аккаунт с id %s не может быть заблокирован по запросу",
                    accountId));
        }
        account.setIsBlocked(true);
        return accountRepository.save(account);
    }

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Аккаунт не найден"));
    }

    @Override
    public void unlockAccount(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            account.setIsBlocked(false);
        } else {
            log.info("Аккаунт с отрицательным балансом не может быть разблокирован");
        }
    }
}
