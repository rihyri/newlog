package com.newlog.backend.controller.member;

import com.newlog.backend.service.mail.MailService;
import com.newlog.backend.dto.mail.PasswordMailDto;
import com.newlog.backend.dto.member.*;
import com.newlog.backend.entity.member.Member;
import com.newlog.backend.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8090")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@Valid @RequestBody JoinRequestDto dto) {
        try {
            memberService.join(dto);
            return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto dto) {
        try {
            LoginResponseDto response = memberService.login(dto);
            return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 아이디 중복 확인
    @GetMapping("/check-id/{memberId}")
    public ResponseEntity<ApiResponse<Boolean>> checkMemberId(@PathVariable("memberId") String memberId) {
        boolean isDuplicate = memberService.checkMemberIdDuplicate(memberId);
        String message = isDuplicate ? "이미 사용중인 아이디입니다." : "사용 가능한 아이디입니다.";
        return ResponseEntity.ok(ApiResponse.success(message, isDuplicate));
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@PathVariable("nickname") String nickname) {
        boolean isDuplicate = memberService.checkNicknameDuplicate(nickname);
        String message = isDuplicate ? "이미 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다.";
        return ResponseEntity.ok(ApiResponse.success(message, isDuplicate));
    }

    // 이메일 중복 확인
    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail (@PathVariable("email") String email) {
        boolean isDuplicate = memberService.checkEmailDuplicate(email);
        String message = isDuplicate ? "이미 사용중인 이메일입니다." : "사용 가능한 이메일입니다.";
        return ResponseEntity.ok(ApiResponse.success(message, isDuplicate));
    }

    // 아이디 찾기 확인
    @PostMapping("/search-id")
    public ResponseEntity<ApiResponse<IdSearchResponseDto>> searchId(@Valid @RequestBody IdSearchRequestDto dto) {
        try {
            IdSearchResponseDto response = memberService.searchId(dto);
            return ResponseEntity.ok(ApiResponse.success("아이디 찾기 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 비밀번호 재설정
    @PostMapping("/search-pwd")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, String>>> searchPwd(@Valid @RequestBody PasswordRequestDto dto) {

        boolean exists = memberService.checkUserByIdAndEmail(dto);

        if (!exists) {
            log.warn("비밀번호 재설정 실패 - 존재하지 않는 회원: ID={}, Email={}", dto.getMemberId(), dto.getEmail());
            return ResponseEntity.badRequest().body(ApiResponse.error("일치하는 회원 정보가 존재하지 않습니다."));
        }

        Member member = memberService.findByUserId(dto.getMemberId());

        // 1. 랜덤 비밀번호 생성
        String newPassword = generateRandomPassword();

        // 2. 이메일 발송
        try {
            PasswordMailDto mailDto = new PasswordMailDto();
            mailDto.setAddress(dto.getEmail());
            mailService.passwordEmailSend(mailDto, newPassword);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("이메일 발송에 실패했습니다."));
        }

        // 3. 비밀번호 업데이트
        memberService.updateEmailPassword(member.getMemberId(), newPassword);

        Map<String, String> responseData = Map.of("memberId", member.getMemberId());
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 이메일로 전송되었습니다.", responseData));
    }

    // 마이페이지
    @PostMapping("/mypage")
    public ResponseEntity<ApiResponse<MyPageResponseDto>> myPage(@Valid @RequestBody MyPageRequestDto dto) {
        try {
            MyPageResponseDto response = memberService.myPage(dto);
            return ResponseEntity.ok(ApiResponse.success("유저 정보 불러오기 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 마이페이지 정보 수정
    @PostMapping("/mypage-update")
    public ResponseEntity<ApiResponse<Void>> join(@Valid @RequestBody MyPageUpdateDto dto) {
        try {
            memberService.myPageUpdate(dto);
            return ResponseEntity.ok(ApiResponse.success("마이페이지 정보수정이 완료되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private String generateRandomPassword() {
        int length = 8;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i ++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
