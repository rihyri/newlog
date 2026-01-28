package com.newlog.backend.service.news;

import com.newlog.backend.dto.news.NewsCommentDto;
import com.newlog.backend.entity.member.Member;
import com.newlog.backend.entity.news.News;
import com.newlog.backend.entity.news.NewsComment;
import com.newlog.backend.jwt.SecurityUtil;
import com.newlog.backend.repository.member.MemberRepository;
import com.newlog.backend.repository.news.NewsCommentRepository;
import com.newlog.backend.repository.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsCommentService {

    private final NewsCommentRepository newsCommentRepository;
    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;

    // 댓글 작성
    @Transactional
    public NewsCommentDto createComment(Long newsNo, String content) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        News news = newsRepository.findByNewsNo(newsNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 뉴스입니다."));

        NewsComment comment = NewsComment.builder()
                .news(news)
                .member(member)
                .content(content)
                .build();

        newsCommentRepository.save(comment);

        return NewsCommentDto.fromEntity(comment, memberId);
    }

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<NewsCommentDto> getCommentsByNewsNo(Long newsNo) {
        List<NewsComment> comments = newsCommentRepository.findByNews_NewsNoOrderByCreatedAtAsc(newsNo);

        String currentMemberId = SecurityUtil.getCurrentMemberId();

        return comments.stream()
                .map(comment -> {
                    return NewsCommentDto.fromEntity(comment, currentMemberId);
                })
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public NewsCommentDto updateComment(Long commentNo, String content) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        NewsComment comment = newsCommentRepository.findById(commentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 작성자 본인만 수정 가능
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }

        comment.setContent(content);

        return NewsCommentDto.fromEntity(comment, memberId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentNo) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        NewsComment comment = newsCommentRepository.findById(commentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }

        newsCommentRepository.delete(comment);
    }
}
