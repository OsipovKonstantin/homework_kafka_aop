package ru.t1.java.demo.mapper;

import org.mapstruct.*;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountCreateDto;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.AccountFullDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {AccountMapper.class})
public interface AccountMapper {
    Account toEntity(AccountDto accountDto);

    @Mapping(target = "balance", constant = "0")
    @Mapping(target = "isBlocked", constant = "false")
    Account toEntity(AccountCreateDto accountCreateDto);

    AccountDto toDto(Account account);

    AccountFullDto toFullDto(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Account partialUpdate(AccountDto accountDto, @MappingTarget Account account);
}
