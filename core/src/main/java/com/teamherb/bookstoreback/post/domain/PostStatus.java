package com.teamherb.bookstoreback.post.domain;

import lombok.Getter;

@Getter
public enum PostStatus {

  SALE("판매 중"),
  RESERVED("예약 중"),
  SOLD_OUT("판매 완료");
      
  private final String name;

  PostStatus(String name) {
    this.name = name;
  }
}
