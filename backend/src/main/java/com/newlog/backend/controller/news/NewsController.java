package com.newlog.backend.controller.news;
import com.newlog.backend.dto.member.ApiResponse;
import com.newlog.backend.dto.news.NewsDto;
import com.newlog.backend.dto.news.NewsViewRequestDto;
import com.newlog.backend.entity.news.News;
import com.newlog.backend.service.news.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8090")
public class NewsController {

    private final NewsService newsService;

    /* 전체 뉴스 조회 */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<NewsDto>>> getNewsList (@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Page<NewsDto> response = newsService.getNewsList(page, size);
            return ResponseEntity.ok(ApiResponse.success("뉴스 리스트 불러오기 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /* 카테고리별 뉴스 목록 조회 */
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<Page<NewsDto>>> getNewsByCategory(@RequestParam("name") String category, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Page<NewsDto> response = newsService.getNewsByCategory(category, page, size);
            return ResponseEntity.ok(ApiResponse.success("카테고리별 뉴스 리스트 불러오기 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /* 뉴스 상세보기 */
    @GetMapping("/view/{newsNo}")
    public ResponseEntity<ApiResponse<NewsDto>> newsDetail(@PathVariable("newsNo") String newsNoStr) {

        Long newsNo = Long.parseLong(newsNoStr);

        try {
            NewsDto response = newsService.newsView(newsNo);
            return ResponseEntity.ok(ApiResponse.success("뉴스 상세정보 불러오기 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
