package com.newlog.backend.service.news;

import com.newlog.backend.dto.member.ApiResponse;
import com.newlog.backend.dto.news.NewsDto;
import com.newlog.backend.dto.news.NewsViewRequestDto;
import com.newlog.backend.entity.news.News;
import com.newlog.backend.entity.news.NewsCategory;
import com.newlog.backend.repository.news.NaverNewsApiResponse;
import com.newlog.backend.repository.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Value("${naver.api.news-url}")
    private String newsApiUrl;

    // 캐시 유지시간 (분)
    private static final int CACHE_MINUTES = 60;

    // 카테고리별 최대 뉴스 개수
    private static final int MAX_NEWS_PER_CATEGORY = 100;


    /* 네이버 뉴스 API 호출 */
    public NaverNewsApiResponse fetchNewsFromNaver(String query, int display, int start, String date) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        String url = UriComponentsBuilder.fromUriString(newsApiUrl)
                .queryParam("query", query)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", "date")
                .build(false)
                .toUriString();

        log.info("네이버 API 호출 URL: {}", url);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NaverNewsApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    NaverNewsApiResponse.class
            );

            NaverNewsApiResponse apiResponse = response.getBody();

            log.info("네이버 API 호출 성공 - query: {}, display: {}", query, display);
            return response.getBody();
        } catch (Exception e) {
            log.error("네이버 뉴스 API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("뉴스 데이터를 가져오는데 실패했습니다.");
        }
    }

    /* 전체 뉴스 목록 조회 */
    @Transactional
    public Page<NewsDto> getNewsList(int page, int size) {
        LocalDateTime cacheTime = LocalDateTime.now().minusMinutes(CACHE_MINUTES);
        long recentNewsCount = newsRepository.countRecentAllNews(cacheTime);

        System.out.println("recentNewsCount : " + recentNewsCount);
        System.out.println("size: " + size);

        if (recentNewsCount < size) {
            log.info("캐시된 전체 뉴스 부족, API 호출");
            NaverNewsApiResponse apiResponse = fetchNewsFromNaver("뉴스", 30, 1, "date");

            if (apiResponse != null && apiResponse.getItems() != null) {
                saveNewsFromApi(apiResponse.getItems(), null);
                cleanupOldNews(null);
            }
        } else {
            log.info("캐시된 전체 뉴스 사용 ({}개)", recentNewsCount);
            cleanupOldNews(null);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<News> newsPage = newsRepository.findAllByOrderByPubDateDesc(pageable);

        return newsPage.map(this::convertToDto);
    }

    /* 카테고리별 뉴스 목록 조회 */
    public Page<NewsDto> getNewsByCategory(String categoryName, int page, int size) {
        NewsCategory category = NewsCategory.fromDisplayName(categoryName);

        if (category == null) {
            throw new IllegalArgumentException("잘못된 카테고리입니다. : " + categoryName);
        }

        LocalDateTime cacheTime = LocalDateTime.now().minusMinutes(CACHE_MINUTES);
        long recentNewsCount = newsRepository.countRecentCategoryNews(category, cacheTime);

        if (recentNewsCount < size) {
            log.info("캐시된 {} 뉴스 부족, API 호출", categoryName);
            NaverNewsApiResponse apiResponse = fetchNewsFromNaver(categoryName, 30, 1, "date");

            if (apiResponse != null && apiResponse.getItems() != null) {
                saveNewsFromApi(apiResponse.getItems(), category);
                cleanupOldNews(category);
            }
        } else {
            log.info("캐시된 {} 뉴스 사용 ({}개)", categoryName, recentNewsCount);
            cleanupOldNews(category);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<News> newsPage = newsRepository.findByCategoryOrderByPubDateDesc(category, pageable);

        return newsPage.map(this::convertToDto);
    }

    /* API 응답 DB 저장 */
    @Transactional
    public void saveNewsFromApi(List<NaverNewsApiResponse.NaverNewsItem> items, NewsCategory category) {
        for (NaverNewsApiResponse.NaverNewsItem item : items) {

            // DB 에 존재하는 뉴스는 skip
            if (newsRepository.findByOriginalLink(item.getOriginallink()).isPresent()) {
                continue;
            }

            String cleanDescription = cleanDescription(item.getDescription());

            if (cleanDescription == null || cleanDescription.trim().isEmpty()) {
                log.warn("유효하지 않는 description - 제목: {}", item.getTitle());
            }

            News news = News.builder()
                    .title(removeHtmlTags(item.getTitle()))
                    .originalLink(item.getOriginallink())
                    .link(item.getLink())
                    .description(cleanDescription)
                    .pubDate(parsePubDate(item.getPubDate()))
                    .category(category)
                    .viewCount(0)
                    .likeCount(0)
                    .build();

            newsRepository.save(news);
        }
    }

    /* 오래된 뉴스 자동 삭제 (100개 초과시) */
    @Transactional
    public void cleanupOldNews(NewsCategory category) {

        long count;

        if (category == null) {
            count = newsRepository.countByCategoryIsNull();
        } else {
            count = newsRepository.countByCategory(category);
        }

        if (count > MAX_NEWS_PER_CATEGORY) {
            int deleteCount = (int) (count - MAX_NEWS_PER_CATEGORY);
            log.info("뉴스 정리 시작 - 카테고리: {}, 현재: {}개, 삭제: {}개", category == null ? "전체" : category.getDisplayName(), count, deleteCount);

            if (category == null) {
                newsRepository.deleteOldestAllNews(deleteCount);
            } else {
                newsRepository.deleteOldestCategoryNews(category, deleteCount);
            }
        }
    }

    private String cleanDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }

        String cleaned = removeHtmlTags(description);

        if (cleaned.trim().matches("^https?://.*")) {
            log.warn("Description이 URL입니다: {}", cleaned);
            return null;
        }

        if (cleaned.trim().length() < 10) {
            return null;
        }

        return cleaned.trim();
    }

    /* HTML 태그 제거 */
    private String removeHtmlTags(String text) {
        if (text == null) return null;

        String cleaned = text.replaceAll("<[^>]+>", "");

        cleaned = cleaned.replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&#39;", "'")
                .replaceAll("&#34;", "\"");

        cleaned = cleaned.replaceAll("\\s+", " ");

        return cleaned.trim();
    }

    /* 문자열 → LocalDateTime */
    private LocalDateTime parsePubDate(String pubDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z",
                    java.util.Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, formatter);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            log.error("날짜 파싱 실패: {}", pubDate);
            return LocalDateTime.now();
        }
    }

    /* entity → DTO */
    private NewsDto convertToDto(News news) {
        return NewsDto.builder()
                .newsNo(news.getNewsNo())
                .title(news.getTitle())
                .originalLink(news.getOriginalLink())
                .link(news.getLink())
                .description(news.getDescription())
                .pubDate(news.getPubDate())
                .category(news.getCategory())
                .categoryDisplayName(news.getCategory() != null ? news.getCategory().getDisplayName() : null)
                .viewCount(news.getViewCount())
                .likeCount(news.getLikeCount())
                .build();
    }

    /* 뉴스 상세페이지 */
    public NewsDto newsView(Long newsNo) {
        News news = newsRepository.findByNewsNo(newsNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 뉴스 넘버입니다."));

        return convertToDto(news);
    }
}
