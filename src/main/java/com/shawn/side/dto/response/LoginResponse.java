package com.shawn.side.dto.response;

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
public class LoginResponse {

    private String token;

    // token 類型應固定為 Bearer
    private String tokenType;

    private Long userId;

    private String email;

    private String name;



}
