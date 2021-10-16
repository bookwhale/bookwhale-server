package com.teamherb.bookstoreback.chatroom.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Opponent {

  private String identity;
  private String profile;

  public Opponent(String identity, String profile) {
    this.identity = identity;
    this.profile = profile;
  }
}
