package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.AccountType;
import ru.t1.java.demo.web.GeneralWebClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class TransactionServiceIntegrationTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    GeneralWebClient generalWebClient;

    @Autowired
    TransactionServiceImpl transactionService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void addTransaction() {
        Client client = new Client("Peter", "Parker", "Spider");
        clientRepository.save(client);
        Account account = new Account(1L, AccountType.CREDIT, BigDecimal.valueOf(5000L), false);
        accountRepository.save(account);

        Transaction transaction = new Transaction(BigDecimal.TEN,
                1L, 1L, false, TransactionType.DEPOSIT);

        Transaction transaction2 = new Transaction(BigDecimal.TEN,
                1L, 1L, false, TransactionType.DEPOSIT);

        List<Transaction> transactions = transactionService.addTransaction(List.of(transaction));
        assertThat(accountRepository.findById(1L).isPresent()).isEqualTo(true);
        assertThat(accountRepository.findById(1L).get().getBalance())
                .isEqualTo(BigDecimal.valueOf(5010L).setScale(2));
        assertThat(transactionRepository.findById(1L).isPresent()).isEqualTo(true);
        assertThat(transactionRepository.findById(1L).get().getAmount()).isEqualTo(BigDecimal.TEN.setScale(2));
        assertThat(transactionRepository.findById(1L).get().getClientId()).isEqualTo(1L);
        assertThat(transactionRepository.findById(1L).get().getAccountId()).isEqualTo(1L);
        assertThat(transactionRepository.findById(1L).get().getIsRetry()).isEqualTo(false);
        assertThat(transactionRepository.findById(1L).get().getType()).isEqualTo(TransactionType.DEPOSIT);
    }
}
