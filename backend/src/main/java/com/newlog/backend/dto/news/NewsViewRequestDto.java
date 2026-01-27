package com.newlog.backend.dto.news;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsViewRequestDto {

    @NotBlank(message = "뉴스 넘버는 필수입니다.")
    private String newsNo;
}
