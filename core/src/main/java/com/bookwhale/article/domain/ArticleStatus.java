package com.bookwhale.article.domain;

import lombok.Getter;

@Getter
public enum ArticleStatus {

    SALE("판매중"),
    RESERVED("예약중"),
    SOLD_OUT("판매완료");

    private final String name;

    ArticleStatus(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.name();
    }
}