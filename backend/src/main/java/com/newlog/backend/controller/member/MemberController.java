package com.newlog.backend.controller.member;

import com.newlog.backend.dto.member.*;
import com.newlog.backend.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8090")
public class MemberController {

    private final MemberService memberService;

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
}
