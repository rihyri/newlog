package com.newlog.backend.repository;

import com.newlog.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId (String memberId);

    boolean existsByMemberId (String memberId);

    boolean existsByNickname (String nickname);

    boolean existsByEmail (String email);

    Optional<Member> findByNicknameAndEmail(String nickname, String email);

    Optional<Member> findByMemberIdAndEmail(String member_id, String email);
}
