package com.bookwhale.article.domain;

import lombok.Getter;

@Getter
public enum BookStatus {

    LOWER("하"),
    MIDDLE("중"),
    UPPER("상"),
    BEST("최상");

    private final String name;

    BookStatus(String name) {
        this.name = name;
    }

    public String getCode() {
        return name();
    }

    public String getName() {
        return name;
    }
}
