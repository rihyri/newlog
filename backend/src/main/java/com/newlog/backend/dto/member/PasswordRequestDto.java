package com.newlog.backend.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequestDto {

    @NotBlank(message = "ID는 필수입니다.")
    private String memberId;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}
