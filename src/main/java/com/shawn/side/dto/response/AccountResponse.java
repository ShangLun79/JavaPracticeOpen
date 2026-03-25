package com.shawn.side.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.shawn.side.entity.Account.AccountStatus;
import com.shawn.side.entity.Account.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private AccountType accountType;

    private AccountStatus status;

    private LocalDateTime createdAt;
    
}
