package ru.t1.java.demo.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionIsAllowedResponse {
    private Boolean isAllowed;
}
