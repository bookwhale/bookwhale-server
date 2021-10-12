package com.teamherb.bookstoreback.chatroom.domain;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.user.domain.User;
import java.time.LocalDateTime;
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
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "room_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id")
  private User buyer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id")
  private User seller;

  @Column(name = "buyer_delete_flag")
  private boolean isBuyerDelete;

  @Column(name = "seller_delete_flag")
  private boolean isSellerDelete;

  @CreatedDate
  private LocalDateTime createdDate;

  private ChatRoom(Post post, User buyer, User seller, boolean isBuyerDelete,
      boolean isSellerDelete) {
    this.post = post;
    this.buyer = buyer;
    this.seller = seller;
    this.isBuyerDelete = isBuyerDelete;
    this.isSellerDelete = isSellerDelete;
  }

  public static ChatRoom create(Post post, User buyer, User seller) {
    return new ChatRoom(post, buyer, seller, false, false);
  }

  private boolean isLoginUserEqualBuyer(User loginUser) {
    return this.buyer.getId().equals(loginUser.getId());
  }

  private boolean isLoginUserEqualSeller(User loginUser) {
    return this.seller.getId().equals(loginUser.getId());
  }

  public void deleteChatRoom(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      this.isBuyerDelete = true;
    } else if (isLoginUserEqualSeller(loginUser)) {
      this.isSellerDelete = true;
    } else {
      throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
    }
  }

  public boolean isLoginUserDelete(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      return isBuyerDelete;
    }
    return isSellerDelete;
  }

  public boolean isOpponentDelete(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      return isSellerDelete;
    }
    return isBuyerDelete;
  }

  public boolean isEmpty() {
    return this.isBuyerDelete && this.isSellerDelete;
  }

  public Opponent getOpponent(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      return new Opponent(this.seller.getIdentity(), this.seller.getProfileImage());
    }
    return new Opponent(this.buyer.getIdentity(), this.buyer.getProfileImage());
  }
}

