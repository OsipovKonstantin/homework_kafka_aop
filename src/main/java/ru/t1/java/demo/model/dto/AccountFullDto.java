package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.t1.java.demo.util.AccountType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AccountFullDto {
    private Long id;
    @JsonProperty("client_id")
    private Long clientId;
    private AccountType type;
    private BigDecimal balance;
    private Boolean isBlocked;
}
