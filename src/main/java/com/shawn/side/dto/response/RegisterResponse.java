package com.shawn.side.dto.response;

import lombok.*;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    
    private Long id;
    private String name;
    private String email;

}
