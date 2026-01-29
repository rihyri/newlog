package com.newlog.backend.repository.customNews;

import com.newlog.backend.entity.news.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomNewsRepository extends JpaRepository<News, Long> {

    // 유저 선호 카테고리 조회
    @Query(value = """
        SELECT n.category 
        FROM news n
        LEFT JOIN news_like l ON n.news_no = l.news_no AND l.member_no = :memberNo
        LEFT JOIN news_view_history v ON n.news_no = v.news_no AND v.member_no = :memberNo
        WHERE n.category IS NOT NULL
        GROUP BY n.category
        ORDER BY (COUNT(DISTINCT l.like_no) * 3 + COUNT(DISTINCT v.history_no) * 1) DESC
        LIMIT 1
        """, nativeQuery = true)
    String findMostPreferredCategory(@Param("memberNo") Long memberNo);

    // 선호 카테고리 뉴스 랜덤 조회
    @Query(value = """
            SELECT * FROM news
                WHERE category = :category
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<News> findRandomNewsByCategory(@Param("category") String category, @Param("limit") int limit);
}
