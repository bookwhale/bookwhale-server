package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.common.domain.BaseEntity;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Post extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id")
  private User seller;

  private String title;

  private String price;

  @Lob
  private String description;

  @Enumerated(EnumType.STRING)
  private PostStatus postStatus;

  @Enumerated(EnumType.STRING)
  private BookStatus bookStatus;

  @Embedded
  private Book book;

  @Builder
  public Post(Long id, User seller, String title, String price, String description,
      PostStatus postStatus, BookStatus bookStatus, Book book) {
    this.id = id;
    this.seller = seller;
    this.title = title;
    this.price = price;
    this.description = description;
    this.postStatus = postStatus;
    this.bookStatus = bookStatus;
    this.book = book;
  }

  public static Post create(User loginUser, PostRequest req) {
    return Post.builder()
        .seller(loginUser)
        .title(req.getTitle())
        .price(req.getPrice())
        .postStatus(PostStatus.SALE)
        .bookStatus(BookStatus.valueOf(req.getBookStatus()))
        .description(req.getDescription())
        .book(Book.create(req.getBookRequest()))
        .build();
  }

  public void update(PostUpdateRequest req) {
    this.title = req.getTitle();
    this.price = req.getPrice();
    this.description = req.getDescription();
    this.bookStatus = BookStatus.valueOf(req.getBookStatus());
  }

  public void validateIsMyPost(User loginUser) {
    if (!this.isMyPost(loginUser)) {
      throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
    }
  }

  public boolean isMyPost(User loginUser) {
    return this.seller.getId().equals(loginUser.getId());
  }

  public void updatePostStatus(String postStatus) {
    this.postStatus = PostStatus.valueOf(postStatus);
  }

  public void validPurchaseRequest() {
    if (this.postStatus.equals(PostStatus.RESERVED) || this.postStatus
        .equals(PostStatus.SOLD_OUT)) {
      throw new CustomException(ErrorCode.INVALID_POST_STATUS);
    }
  }



}
