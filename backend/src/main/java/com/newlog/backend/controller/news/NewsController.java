package com.newlog.backend.controller.news;
import com.newlog.backend.dto.member.ApiResponse;
import com.newlog.backend.dto.news.NewsCommentDto;
import com.newlog.backend.dto.news.NewsDto;
import com.newlog.backend.dto.news.NewsViewRequestDto;
import com.newlog.backend.entity.news.News;
import com.newlog.backend.service.news.NewsCommentService;
import com.newlog.backend.service.news.NewsLikeService;
import com.newlog.backend.service.news.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8090")
public class NewsController {

    private final NewsService newsService;
    private final NewsLikeService newsLikeService;
    private final NewsCommentService newsCommentService;


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

    /* 뉴스 좋아요 토글 */
    @PostMapping("/like/{newsNo}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(@PathVariable("newsNo") Long newsNo) {
        try {
            boolean isLiked = newsLikeService.toggleLike(newsNo);

            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);

            return ResponseEntity.ok(ApiResponse.success(
                    isLiked ? "좋아요를 추가했습니다." : "좋아요를 취소했습니다.", response));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /* 좋아요 여부 확인 */
    @GetMapping("/like/{newsNo}/status")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getLikeStatus (@PathVariable("newsNo") Long newsNo) {
        boolean isLiked = newsLikeService.isLikedByCurrentUser(newsNo);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", isLiked);

        return ResponseEntity.ok(ApiResponse.success("좋아요 상태 조회 성공", response));
    }

    /* 댓글 작성 */
    @PostMapping("/{newsNo}/comments")
    public ResponseEntity<ApiResponse<NewsCommentDto>> createComment(
            @PathVariable("newsNo") Long newsNo, @RequestBody Map<String, String> request
    ) {
        try {
            String content = request.get("content");
            NewsCommentDto comment = newsCommentService.createComment(newsNo, content);
            return ResponseEntity.ok(ApiResponse.success("댓글 작성 성공", comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /* 댓글 목록 조회 */
    @GetMapping("/{newsNo}/comments")
    public ResponseEntity<ApiResponse<List<NewsCommentDto>>> getComments (
            @PathVariable("newsNo") Long newsNo
    ) {
        List<NewsCommentDto> comments = newsCommentService.getCommentsByNewsNo(newsNo);
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공", comments));
    }

    /* 댓글 수정 */
    @PutMapping("/comments/{commentNo}")
    public ResponseEntity<ApiResponse<NewsCommentDto>> updateComment(
            @PathVariable("commentNo") Long commentNo, @RequestBody Map<String, String> request
    ) {
        try {
            String content = request.get("content");
            NewsCommentDto comment = newsCommentService.updateComment(commentNo, content);
            return ResponseEntity.ok(ApiResponse.success("댓글 수정 성공", comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /* 댓글 삭제 */
    @DeleteMapping("/comments/{commentNo}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("commentNo") Long commentNo) {
        try {
            newsCommentService.deleteComment(commentNo);
            return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공",null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
