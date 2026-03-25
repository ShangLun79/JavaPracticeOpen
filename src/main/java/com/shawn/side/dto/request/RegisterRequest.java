package com.shawn.side.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message= "Email 不可為空")
    @Email(message= "Email 格式不正確")
    @Size(max= 100, message= "Email 長度不可超過 100 字元")
    private String email;

    @NotBlank(message= "密碼 不可為空")
    @Size(min = 6, max = 100, message = "密碼長度必須介於 6 到 100 字元")
    private String password;

    @NotBlank(message= " 姓名不可為空")
    @Size(max = 50, message = "姓名長度不可超過 50 字元")
    private String name;
}
