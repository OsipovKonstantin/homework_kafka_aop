package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.AccountType;
import ru.t1.java.demo.web.GeneralWebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceUnitTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private KafkaTransactionProducer kafkaTransactionProducer;

    @Mock
    private GeneralWebClient generalWebClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    @Test
    void testParseJson_success() throws IOException {
        List<TransactionDto> transactions = transactionService.parseJson();

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
    }

    @Test
    void testAddTransaction_success() {
        Transaction transaction = new Transaction();
        transaction.setClientId(1L);
        transaction.setAccountId(2L);
        transaction.setAmount(new BigDecimal("100"));

        Account account = new Account();
        account.setBalance(new BigDecimal("500"));
        account.setClientId(1L);
        account.setType(AccountType.CREDIT);
        account.setIsBlocked(false);

        Client client = new Client();
        client.setId(1L);

        TransactionServiceImpl spyTransactionService = spy(transactionService);

        when(spyTransactionService.isTransactionAllowed(transaction.getClientId())).thenReturn(Boolean.TRUE);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        List<Transaction> result = spyTransactionService.addTransaction(List.of(transaction));

        assertNotNull(result);
    }

    @Test
    void testCancelTransaction_success() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("-100"));
        transaction.setAccountId(2L);

        Account account = new Account();
        account.setBalance(new BigDecimal("500"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));

        transactionService.cancelTransaction(List.of(1L));

        verify(transactionRepository, times(1)).deleteAllById(List.of(1L));
        verify(accountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetTransaction_success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransaction(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetTransaction_notFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> transactionService.getTransaction(1L));

        assertEquals("Транзакция с id 1 не найдена", exception.getMessage());
    }
}