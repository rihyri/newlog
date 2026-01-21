package com.newlog.backend.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageUpdateDto {

    @NotBlank(message = "아이디는 필수입니다.")
    private String memberId;

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String memberPw;

    private String newMemberPw;
    private String memberPwChk;
    private String nickname;
    private String email;
}
