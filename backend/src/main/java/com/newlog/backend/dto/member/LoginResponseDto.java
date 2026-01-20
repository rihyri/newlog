package com.newlog.backend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";
    private String memberId;
    private String nickname;

}
