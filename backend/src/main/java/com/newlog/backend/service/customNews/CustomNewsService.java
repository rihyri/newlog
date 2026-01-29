package com.newlog.backend.service.customNews;

import com.newlog.backend.dto.news.NewsDto;
import com.newlog.backend.entity.member.Member;
import com.newlog.backend.entity.news.News;
import com.newlog.backend.jwt.SecurityUtil;
import com.newlog.backend.repository.customNews.CustomNewsRepository;
import com.newlog.backend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomNewsService {

    private final CustomNewsRepository customNewsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<NewsDto> getCustomNewsList() {
        String memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Long memberNo = member.getMemberNo();

        // 회원 선호 카테고리 조회
        String preferredCategory = getMostPreferredCategory(memberNo);

        if (preferredCategory == null) {
            return Collections.emptyList();
        }

        // 선호 카테고리 뉴스 랜덤 조회
        List<News> newsList = customNewsRepository.findRandomNewsByCategory(preferredCategory, 5);

        return newsList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "preferredCategory", key = "#memberNo")
    public String getMostPreferredCategory(Long memberNo) {
        return customNewsRepository.findMostPreferredCategory(memberNo);
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
}
