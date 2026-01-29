package com.newlog.backend.controller.customNews;

import com.newlog.backend.dto.member.ApiResponse;
import com.newlog.backend.dto.news.NewsDto;
import com.newlog.backend.repository.customNews.CustomNewsRepository;
import com.newlog.backend.service.customNews.CustomNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom-news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8090")
public class CustomNewsController {

    private final CustomNewsService customNewsService;
    private final CustomNewsRepository customNewsRepository;

    /* 커스텀 뉴스 조회 */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<NewsDto>>> getCustomNewsList() {
        try {
            List<NewsDto> response = customNewsService.getCustomNewsList();
            return ResponseEntity.ok(ApiResponse.success("커스텀 뉴스 리스트 불러오기 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
