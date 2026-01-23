package com.newlog.backend.repository.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverNewsApiResponse {
    private String lastBuildDate;
    private Integer total;
    private Integer start;
    private Integer display;
    private List<NaverNewsItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaverNewsItem {
        private String title;
        private String originallink;
        private String link;
        private String description;
        private String pubDate;
    }
}