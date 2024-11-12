package ru.t1.java.demo.mapper;

import org.mapstruct.*;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.dto.TransactionFullDto;
import ru.t1.java.demo.model.dto.TransactionType;

import java.math.BigDecimal;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {TransactionMapper.class})
public interface TransactionMapper {
    @Mapping(target = "isRetry", constant = "false")
    @Mapping(target = "type", expression = "java(determineTransactionType(transactionDto.getAmount()))")
    Transaction toEntity(TransactionDto transactionDto);

    default TransactionType determineTransactionType(BigDecimal amount) {
        return (amount.compareTo(BigDecimal.ZERO) >= 0) ? TransactionType.DEPOSIT : TransactionType.WITHDRAW;
    }

    TransactionDto toDto(Transaction transaction);

    TransactionFullDto toFullDto(Transaction transaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Transaction partialUpdate(TransactionDto transactionDto, @MappingTarget Transaction transaction);
}
