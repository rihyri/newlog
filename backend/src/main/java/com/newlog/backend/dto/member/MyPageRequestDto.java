package com.newlog.backend.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageRequestDto {

    @NotBlank(message = "아이디는 필수입니다.")
    private String memberId;

}
