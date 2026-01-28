package com.newlog.backend.repository.news;

import com.newlog.backend.entity.news.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
    List<NewsComment> findByNews_NewsNoOrderByCreatedAtAsc(Long newsNo);

    int countByNews_NewsNo(Long newsNo);
}
