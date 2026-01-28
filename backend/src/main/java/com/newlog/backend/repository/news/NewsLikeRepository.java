package com.newlog.backend.repository.news;

import com.newlog.backend.entity.news.NewsLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsLikeRepository extends JpaRepository<NewsLike, Long> {
    Optional<NewsLike> findByNews_NewsNoAndMember_MemberNo(Long newsNo, Long memberNo);

    boolean existsByNews_NewsNoAndMember_MemberNo(Long newsNo, Long memberNo);
}
