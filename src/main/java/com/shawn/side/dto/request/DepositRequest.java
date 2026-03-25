package com.shawn.side.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DepositRequest {

    @NotNull(message = "帳戶 ID 不可為空")
    private Long accountId;

    @NotNull(message = "金額不可為空")
    @DecimalMin(value = "0.01", message = "金額必須大於 0")
    private BigDecimal amount;

    private String description;
}
