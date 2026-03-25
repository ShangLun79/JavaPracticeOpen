package com.shawn.side.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.shawn.side.entity.Transaction.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;

    private TransactionType transactionType;

    private BigDecimal amount;

    private String fromAccountNumber;

    private String toAccountNumber;

    private String description;

    private LocalDateTime createdAt;

}
