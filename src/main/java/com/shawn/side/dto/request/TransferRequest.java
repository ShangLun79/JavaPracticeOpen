package com.shawn.side.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class TransferRequest {

    @NotNull(message = "來源帳戶 ID 不可為空")
    private Long fromAccountId;

    @NotNull(message = "目標帳戶 ID 不可為空")
    private Long toAccountId;

    @NotNull(message = "金額不可為空")
    @DecimalMin(value = "0.01", message = "金額必須大於 0")
    private BigDecimal amount;

    private String description;
}
