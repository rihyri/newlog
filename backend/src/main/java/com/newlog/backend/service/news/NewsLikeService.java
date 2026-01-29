package com.newlog.backend.service.news;

import com.newlog.backend.entity.member.Member;
import com.newlog.backend.entity.news.News;
import com.newlog.backend.entity.news.NewsLike;
import com.newlog.backend.jwt.SecurityUtil;
import com.newlog.backend.repository.member.MemberRepository;
import com.newlog.backend.repository.news.NewsLikeRepository;
import com.newlog.backend.repository.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsLikeService {

    private final NewsLikeRepository newsLikeRepository;
    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean toggleLike(Long newsNo) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Long memberNo = member.getMemberNo();

        News news = newsRepository.findById(newsNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 뉴스입니다."));

        // 좋아요 캐시 삭제
        evictPreferredCategoryCache(memberNo);

        // 이미 '좋아요'한 경우 -> 좋아요 취소
        Optional<NewsLike> existingLike = newsLikeRepository.findByNews_NewsNoAndMember_MemberNo(newsNo, memberNo);

        if (existingLike.isPresent()) {
            newsLikeRepository.delete(existingLike.get());
            news.decrementLikeCount();
            return false;
        } else {
            NewsLike newsLike = NewsLike.builder()
                    .news(news)
                    .member(member)
                    .build();
            newsLikeRepository.save(newsLike);
            news.incrementLikeCount();
            return true;
        }
    }

    // 뉴스에 대한 유저의 좋아요 여부 확인
    public boolean isLikedByCurrentUser(Long newsNo) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            return false;
        }

        Member member = memberRepository.findByMemberId(memberId).orElse(null);

        if (member == null) {
            return false;
        }

        return newsLikeRepository.existsByNews_NewsNoAndMember_MemberNo(newsNo, member.getMemberNo());
    }

    @CacheEvict(value = "preferredCategory", key = "#memberNo")
    public void evictPreferredCategoryCache(Long memberNo) {
    }
}
