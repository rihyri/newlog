package com.newlog.backend.dto.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.newlog.backend.entity.member.Member;
import com.newlog.backend.entity.news.NewsComment;
import com.newlog.backend.jwt.SecurityUtil;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCommentDto {

    private Long commentNo;
    private Long newsNo;
    private Long memberNo;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty("isAuthor")
    private boolean isAuthor;

    @JsonProperty("isAuthor")
    public boolean getIsAuthor() {
        return this.isAuthor;
    }

    public static NewsCommentDto fromEntity(NewsComment comment, String memberId) {

        String longinId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return NewsCommentDto.builder()
                .commentNo(comment.getCommentNo())
                .newsNo(comment.getNews().getNewsNo())
                .memberNo(comment.getMember().getMemberNo())
                .nickname(comment.getMember().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isAuthor(longinId != null && longinId.equals(comment.getMember().getMemberId()))
                .build();

    }
}
