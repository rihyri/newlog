package com.newlog.backend.entity.news;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NEWS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_no")
    private Long newsNo;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "original_link", nullable = false, unique = true, length = 1000)
    private String originalLink;

    @Column(name = "link", nullable = false, length = 1000)
    private String link;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "pub_date", nullable = false)
    private LocalDateTime pubDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private NewsCategory category;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsViewHistory> viewHistories = new ArrayList<>();

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
