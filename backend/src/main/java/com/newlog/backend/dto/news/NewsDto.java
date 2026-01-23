package com.newlog.backend.dto.news;

import com.newlog.backend.entity.news.NewsCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {

    private Long newsNo;
    private String title;
    private String originalLink;
    private String link;
    private String description;
    private LocalDateTime pubDate;
    private NewsCategory category;
    private String categoryDisplayName;
    private Integer viewCount;
    private Integer likeCount;
    private Boolean isLiked;
}
