package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> parseJson();

    List<Transaction> addTransaction(List<Transaction> transactions);

    void cancelTransaction(List<Long> transactionIds);

    Transaction getTransaction(Long transactionId);

    Boolean isTransactionAllowed(Long clientId);
}
