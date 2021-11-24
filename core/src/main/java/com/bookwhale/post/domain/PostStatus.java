package com.bookwhale.post.domain;

import lombok.Getter;

@Getter
public enum PostStatus {

  SALE("판매중"),
  RESERVED("예약중"),
  SOLD_OUT("판매완료");

  private final String name;

  PostStatus(String name) {
    this.name = name;
  }

  public String getCode() {
    return this.name();
  }
}
