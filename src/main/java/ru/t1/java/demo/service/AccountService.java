package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.AccountFullDto;

import java.util.List;

public interface AccountService {
    void addAccount(List<Account> accounts);

    List<AccountDto> parseJson();

    Account registerAccount(Account account);

    Account getAccountById(Long accountId);

    Account blockDebitAccount(Long accountId);

    void unlockAccount(Account account);
}
