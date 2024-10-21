package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.exception.InvalidException;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.kafka.KafkaAccountProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.util.AccountType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private KafkaAccountProducer kafkaAccountProducer;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testAddAccount_success() {
        // Настраиваем мок-данные
        Account account = new Account();
        account.setId(1L);

        List<Account> accounts = List.of(account);
        when(accountRepository.saveAll(accounts)).thenReturn(accounts);

        accountService.addAccount(accounts);

        verify(accountRepository, times(1)).saveAll(accounts);
        verify(kafkaAccountProducer, times(1)).send(1L);
    }

    @Test
    void testParseJson_success() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<AccountDto> mockAccounts = new ArrayList<>();
        mockAccounts.add(new AccountDto());
        mockAccounts.add(new AccountDto());

        AccountServiceImpl spyService = spy(accountService);
        doReturn(mockAccounts).when(spyService).parseJson();

        List<AccountDto> accounts = spyService.parseJson();

        assertNotNull(accounts);
        assertEquals(2, accounts.size());
    }

    @Test
    void testRegisterAccount_success() {
        Account account = new Account();
        when(accountRepository.save(account)).thenReturn(account);

        Account savedAccount = accountService.registerAccount(account);

        assertNotNull(savedAccount);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testGetAccountById_success() {
        Account account = new Account();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account foundAccount = accountService.getAccountById(1L);

        assertNotNull(foundAccount);
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAccountById_notFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getAccountById(1L));
    }

    @Test
    void testBlockDebitAccount_success() {
        Account account = new Account();
        account.setType(AccountType.DEBIT);
        account.setIsBlocked(false);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Account blockedAccount = accountService.blockDebitAccount(1L);

        assertTrue(blockedAccount.getIsBlocked());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testBlockDebitAccount_invalidAccountType() {
        Account account = new Account();
        account.setType(AccountType.CREDIT);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InvalidException.class, () -> accountService.blockDebitAccount(1L));
    }

    @Test
    void testUnlockAccount_success() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100"));
        account.setIsBlocked(true);

        accountService.unlockAccount(account);

        assertFalse(account.getIsBlocked());
    }

    @Test
    void testUnlockAccount_negativeBalance() {
        Account account = new Account();
        account.setBalance(new BigDecimal("-100"));
        account.setIsBlocked(true);

        accountService.unlockAccount(account);

        assertTrue(account.getIsBlocked());
    }
}
