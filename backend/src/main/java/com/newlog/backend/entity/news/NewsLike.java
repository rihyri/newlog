package com.newlog.backend.entity.news;

import com.newlog.backend.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "NEWS_LIKE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NewsLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_no")
    private Long likeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_no", nullable = false)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @CreationTimestamp
    @Column(name = "crated_at", nullable = false)
    private LocalDateTime createdAt;
}
