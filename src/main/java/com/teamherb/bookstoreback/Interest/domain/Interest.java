package com.teamherb.bookstoreback.Interest.domain;

import com.teamherb.bookstoreback.common.domain.BaseEntity;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Interest extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "interest_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  private Interest(User user, Post post) {
    this.user = user;
    this.post = post;
  }

  public static Interest create(User loginUser, Post post) {
    return new Interest(loginUser, post);
  }

  public void validateIsMyInterest(User loginUser) {
    if (!isMyInterest(loginUser)) {
      throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
    }
  }

  public boolean isMyInterest(User loginUser) {
    return this.getUser().getId().equals(loginUser.getId());
  }
}
