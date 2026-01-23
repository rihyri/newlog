package com.newlog.backend.repository.news;

import com.newlog.backend.entity.news.News;
import com.newlog.backend.entity.news.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findByOriginalLink(String originalLink);

    Page<News> findAllByOrderByPubDateDesc(Pageable pageable);

    Page<News> findByCategoryOrderByPubDateDesc(NewsCategory category, Pageable pageable);

    // 최근 캐시된 뉴스가 있는지 확인
    @Query("SELECT COUNT(n) FROM News n WHERE n.category IS NULL AND n.createdAt > :cacheTime")
    long countRecentAllNews(@Param("cacheTime") LocalDateTime cachedTime);

    @Query("SELECT COUNT(n) FROM News n WHERE n.category = :category AND n.createdAt > :cacheTime")
    long countRecentCategoryNews(@Param("category") NewsCategory category, @Param("cacheTime") LocalDateTime cacheTime);

    // 뉴스 개수 count
    long countByCategory(NewsCategory category);
    long countByCategoryIsNull();

    // 오래된 뉴스 삭제 (좋아요, 댓글 없는 것만)
    @Modifying
    @Query("DELETE FROM News n WHERE n.category IS NULL AND n.newsNo IN " +
            "(SELECT n2.newsNo FROM News n2 " +
            "WHERE n2.category IS NULL " +
            "AND n2.likeCount = 0 AND n2.viewCount = 0 " +
            "AND NOT EXISTS (SELECT 1 FROM NewsComment c WHERE c.news.newsNo = n2.newsNo) " +
            "ORDER BY n2.createdAt ASC)")
    void deleteOldestAllNews(@Param("limit") int limit);

    @Modifying
    @Query("DELETE FROM News n WHERE n.category = :category AND n.newsNo IN " +
            "(SELECT n2.newsNo FROM News n2 " +
            "WHERE n2.category = :category " +
            "AND n2.likeCount = 0 AND n2.viewCount = 0 " +
            "AND NOT EXISTS (SELECT 1 FROM NewsComment c WHERE c.news.newsNo = n2.newsNo) " +
            "ORDER BY n2.createdAt ASC)")
    void deleteOldestCategoryNews(@Param("category") NewsCategory category, @Param("limit") int limit);

}
