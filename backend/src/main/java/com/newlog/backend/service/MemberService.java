package com.newlog.backend.service;

import com.newlog.backend.dto.member.*;
import com.newlog.backend.entity.Member;
import com.newlog.backend.jwt.JwtTokenProvider;
import com.newlog.backend.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public void join (JoinRequestDto dto) {
        if (!dto.getMemberPw().equals(dto.getMemberPwChk())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.existsByMemberId(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        Member member = Member.builder()
                .memberId(dto.getMemberId())
                .memberPw(passwordEncoder.encode(dto.getMemberPw()))
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .role(Member.Role.USER)
                .isActive(true)
                .build();

        memberRepository.save(member);
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(dto.getMemberPw(), member.getMemberPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (!member.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        String token = jwtTokenProvider.createToken(member.getMemberId(), member.getRole().name());

        return new LoginResponseDto(token, "Bearer", member.getMemberId(), member.getNickname());
    }

    // 아이디 중복 체크
    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    // 닉네임 중복 체크
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
    
    // 이메일 중복 체크
    public boolean checkEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 닉네임, 이메일로 아이디 찾기
    public IdSearchResponseDto searchId(IdSearchRequestDto dto) {
        Member member = memberRepository.findByNicknameAndEmail(dto.getNickname(), dto.getEmail()).orElseThrow(() -> new IllegalArgumentException("닉네임, 이메일과 일치하는 아이디가 존재하지 않습니다."));

        return new IdSearchResponseDto(member.getMemberId());
    }

    // ID, 이메일로 유저 정보 찾기
    public boolean checkUserByIdAndEmail(PasswordRequestDto dto) {
        return memberRepository.findByMemberIdAndEmail(dto.getMemberId(), dto.getEmail())
                .isPresent();
    }

    // ID 로 유저 찾기
    public Member findByUserId(String memberId) {

        return memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("ID와 일치하는 유저가 존재하지 않습니다."));
    }

    // 이메일로 전송된 텍스트로 비밀번호 변경
    @Transactional
    public void updateEmailPassword(String memberId, String password) {

        if (password.isEmpty()) {
            throw new IllegalArgumentException("변경할 비밀번호가 존재하지 않습니다.");
        }

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("ID와 일치하는 유저가 존재하지 않습니다."));

        member.setMemberPw(passwordEncoder.encode(password));

        memberRepository.save(member);
    }
}
