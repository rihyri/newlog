package com.newlog.backend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MyPageResponseDto {

    private String memberId;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;

}
