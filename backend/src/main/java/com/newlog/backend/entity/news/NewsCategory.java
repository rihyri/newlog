package com.newlog.backend.entity.news;

import lombok.Getter;

@Getter
public enum NewsCategory {

    POLITICS("정치"),
    ECONOMY("경제"),
    SOCIETY("사회"),
    CULTURE("생활/문화"),
    IT_SCIENCE("IT/과학"),
    WORLD("세계");

    private final String displayName;

    NewsCategory(String displayName) {
        this.displayName = displayName;
    }

    public static NewsCategory fromDisplayName(String displayName) {
        for (NewsCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        return null;
    }
}
