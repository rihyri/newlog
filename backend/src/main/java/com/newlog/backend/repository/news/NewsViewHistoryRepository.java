package com.newlog.backend.repository.news;

import com.newlog.backend.entity.news.NewsViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsViewHistoryRepository extends JpaRepository<NewsViewHistory, Long> {
    boolean existsByNews_NewsNoAndMember_MemberNo(Long newsNo, Long memberNo);
}
