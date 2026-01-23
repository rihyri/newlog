package com.newlog.backend.service.member;

import com.newlog.backend.dto.member.*;
import com.newlog.backend.entity.member.Member;
import com.newlog.backend.jwt.JwtTokenProvider;
import com.newlog.backend.repository.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

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

    // 마이페이지 정보
    public MyPageResponseDto myPage(MyPageRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getMemberId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));

        if (!member.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        return new MyPageResponseDto(member.getMemberId(), member.getNickname(), member.getEmail(), member.getCreatedAt());
    }
    
    // 마이페이지 정보 수정
    @Transactional
    public void myPageUpdate(MyPageUpdateDto dto) {

        String memberId = dto.getMemberId();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("ID와 일치하는 유저가 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getMemberPw(), member.getMemberPw())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!dto.getNewMemberPw().trim().isEmpty() && !dto.getMemberPw().trim().isEmpty()) {

            String newPw = dto.getNewMemberPw().trim();

            System.out.println("타는지 확인 ===");
            System.out.println("newPw: " + newPw);

            if (newPw.length() < 8 || newPw.length() > 20) {
                throw new IllegalArgumentException("비밀번호는 8~20자 사이여야 합니다.");
            }

            Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&^])[A-Za-z\\d@$!%*#?&^]+$");
            if (!pattern.matcher(newPw).matches()) {
                throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.");
            }

            if (!newPw.equals(dto.getMemberPwChk())) {
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
            }
            member.setMemberPw(passwordEncoder.encode(newPw));
        }

        if (dto.getNickname() != null && !dto.getNickname().trim().isEmpty()) {

            String nickname = dto.getNickname().trim();

            if (nickname.length() < 2 || nickname.length() > 10) {
                throw new IllegalArgumentException("닉네임은 2~10자 사이여야 합니다.");
            }

            if (!nickname.equals(member.getNickname())) {
                if (memberRepository.existsByNickname(dto.getNickname())) {
                    throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
                }
                member.setNickname(nickname);
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {

            String email = dto.getEmail().trim();

            Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

            if (!emailPattern.matcher(email).matches()) {
                throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
            }

            if (!email.equals(member.getEmail())) {
                if (memberRepository.existsByEmail(dto.getEmail())) {
                    throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
                }
                member.setEmail(email);
            }
        }

        memberRepository.save(member);
    }
}
