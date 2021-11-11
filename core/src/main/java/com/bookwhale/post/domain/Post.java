package com.bookwhale.post.domain;

import static com.bookwhale.post.domain.PostStatus.RESERVED;
import static com.bookwhale.post.domain.PostStatus.SOLD_OUT;

import com.bookwhale.common.domain.BaseEntity;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.image.domain.Images;
import com.bookwhale.user.domain.User;
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

  @Enumerated(EnumType.STRING)
  private Location sellingLocation;

  private Long likeCount = 0L;

  private Long viewCount = 0L;

  @Embedded
  private Book book;

  @Embedded
  private final Images images = Images.empty();

  @Builder
  public Post(Long id, User seller, String title, String price, String description,
      PostStatus postStatus, BookStatus bookStatus,
      Location sellingLocation, Long likeCount, Long viewCount, Book book) {
    this.id = id;
    this.seller = seller;
    this.title = title;
    this.price = price;
    this.description = description;
    this.postStatus = postStatus;
    this.bookStatus = bookStatus;
    this.sellingLocation = sellingLocation;
    this.likeCount = likeCount == null ? 0L : likeCount;
    this.viewCount = viewCount == null ? 0L : viewCount;
    this.book = book;
  }

  public static Post create(User loginUser, Post post) {
    return Post.builder()
        .seller(loginUser)
        .title(post.getTitle())
        .price(post.getPrice())
        .postStatus(PostStatus.SALE)
        .bookStatus(post.getBookStatus())
        .description(post.getDescription())
        .book(Book.create(post.getBook()))
        .sellingLocation(post.getSellingLocation())
        .likeCount(post.getLikeCount())
        .viewCount(post.getViewCount())
        .build();
  }

  public void update(Post post) {
    this.title = post.getTitle();
    this.price = post.getPrice();
    this.description = post.getDescription();
    this.bookStatus = post.getBookStatus();
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

  public void validatePostStatus() {
    if (this.postStatus.equals(RESERVED) || this.postStatus.equals(SOLD_OUT)) {
      throw new CustomException(ErrorCode.INVALID_POST_STATUS);
    }
  }

  public void increaseOneViewCount() {
    this.viewCount += 1L;
  }

  public void increaseOneLikeCount() {
    this.likeCount += 1L;
  }

  public void decreaseOneLikeCount() {
    if (this.likeCount > 0) {
      this.likeCount -= 1;
    }
  }

  @Override
  public String toString() {
    return "Post{" +
        "id=" + id +
        ", seller=" + seller +
        ", title='" + title + '\'' +
        ", price='" + price + '\'' +
        ", description='" + description + '\'' +
        ", postStatus=" + postStatus +
        ", bookStatus=" + bookStatus +
        ", sellingLocation=" + sellingLocation +
        ", likeCount=" + likeCount +
        ", viewCount=" + viewCount +
        ", book=" + book +
        ", images=" + images +
        '}';
  }
}
