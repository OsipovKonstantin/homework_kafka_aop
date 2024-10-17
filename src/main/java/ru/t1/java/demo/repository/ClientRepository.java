package ru.t1.java.demo.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.aop.Transaction;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Client> findById(Long aLong);
}