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

  @Column(name = "buyer_leave")
  private boolean isBuyerLeave;

  @Column(name = "seller_leave")
  private boolean isSellerLeave;

  @CreatedDate
  private LocalDateTime createdDate;

  private ChatRoom(Post post, User buyer, User seller, boolean isBuyerLeave,
      boolean isSellerLeave) {
    this.post = post;
    this.buyer = buyer;
    this.seller = seller;
    this.isBuyerLeave = isBuyerLeave;
    this.isSellerLeave = isSellerLeave;
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

  public void leaveChatRoom(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      this.isBuyerLeave = true;
    } else if (isLoginUserEqualSeller(loginUser)) {
      this.isSellerLeave = true;
    } else {
      // 채팅방의 참여자가 아니면 에러를 반환한다.
      throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
    }
  }

  public boolean checkIsLeaveChatRoom(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      return isBuyerLeave;
    }
    return isSellerLeave;
  }

  public boolean checkIsOpponentLeaveChatRoom(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      return isSellerLeave;
    }
    return isBuyerLeave;
  }

  public boolean isEmptyChatRoom() {
    return this.isBuyerLeave && this.isSellerLeave;
  }

  public Opponent getOpponent(User loginUser) {
    if (isLoginUserEqualBuyer(loginUser)) {
      return new Opponent(this.seller.getIdentity(), this.seller.getProfileImage());
    }
    return new Opponent(this.buyer.getIdentity(), this.buyer.getProfileImage());
  }
}

