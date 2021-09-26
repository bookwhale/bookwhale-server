package com.teamherb.bookstoreback.chatroom.service;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.chatroom.domain.ChatRoom;
import com.teamherb.bookstoreback.chatroom.domain.ChatRoomRepository;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomCreateRequest;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomResponse;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.mail.MailUtil;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("채팅방 단위 테스트(Service)")
public class ChatRoomServiceTest {

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private MailUtil mailUtil;

  ChatRoomService chatRoomService;

  User buyer;

  User seller;

  Post post;

  @BeforeEach
  void setUp() {
    chatRoomService = new ChatRoomService(chatRoomRepository, userRepository, postRepository,
        mailUtil);

    buyer = User.builder()
        .id(1L)
        .identity("highright96")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();

    seller = User.builder()
        .id(2L)
        .identity("hose12")
        .name("주호세")
        .email("hose12@email.com")
        .phoneNumber("010-5678-5678")
        .build();

    post = Post.create(seller, PostRequest.builder()
        .bookRequest(BookRequest.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("토비의 스프링")
            .bookPublisher("출판사")
            .bookAuthor("이일민")
            .build())
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build());
  }

  @DisplayName("거래 요청 메일을 보낸 후 채팅방을 생성한다.")
  @Test
  void createPost() {
    ChatRoom chatRoom = ChatRoom.create(post, buyer, seller);

    when(userRepository.findById(any())).thenReturn(of(seller));
    when(postRepository.findById(any())).thenReturn(of(post));
    doNothing().when(mailUtil).sendCreateChatRoomMailToSeller(any(), any(), any());
    when(chatRoomRepository.save(any())).thenReturn(chatRoom);

    chatRoomService.createChatRoom(buyer,
        ChatRoomCreateRequest.builder().sellerId(1L).postId(1L).build());

    verify(userRepository).findById(any());
    verify(postRepository).findById(any());
    verify(mailUtil).sendCreateChatRoomMailToSeller(any(), any(), any());
    verify(chatRoomRepository).save(any());
  }

  @DisplayName("채팅방을 생성할 때 게시글 상태가 판매완료이면 예외가 발생한다.")
  @Test
  void createPost_postStatus_soldOut_failure() {
    post.updatePostStatus(PostStatus.SOLD_OUT.toString());

    when(userRepository.findById(any())).thenReturn(of(seller));
    when(postRepository.findById(any())).thenReturn(of(post));

    assertThatThrownBy(() -> chatRoomService.createChatRoom(buyer,
        ChatRoomCreateRequest.builder().sellerId(1L).postId(1L).build()))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_STATUS.getMessage());
  }

  @DisplayName("채팅방을 생성할 때 게시글 상태가 예약중이면 예외가 발생한다.")
  @Test
  void createPost_postStatus_reserved_failure() {
    post.updatePostStatus(PostStatus.RESERVED.toString());

    when(userRepository.findById(any())).thenReturn(of(seller));
    when(postRepository.findById(any())).thenReturn(of(post));

    assertThatThrownBy(() -> chatRoomService.createChatRoom(buyer,
        ChatRoomCreateRequest.builder().sellerId(1L).postId(1L).build()))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_STATUS.getMessage());
  }

  /*
   buyer : 로그인한 유저
   seller : 상대방
  */
  @DisplayName("채팅방들을 조회한다.")
  @Test
  void findChatRooms() {
    // 로그인한 유저와 상대방 모두 존재하는 채팅방
    ChatRoom chatRoom = ChatRoom.create(post, buyer, seller);

    // 로그인한 유저만 존재하는 채팅방
    ChatRoom chatRoomOpponentLeave = ChatRoom.create(post, buyer, seller);
    chatRoomOpponentLeave.leaveChatRoom(seller);

    // 상대방만 존재하는 채팅방
    ChatRoom chatRoomUserLeave = ChatRoom.create(post, buyer, seller);
    chatRoomUserLeave.leaveChatRoom(buyer);

    List<ChatRoom> rooms = List.of(chatRoom, chatRoomOpponentLeave, chatRoomUserLeave);

    when(chatRoomRepository.findAllByBuyerOrSellerCreatedDateDesc(any())).thenReturn(rooms);

    List<ChatRoomResponse> responses = chatRoomService.findChatRooms(buyer);

    verify(chatRoomRepository).findAllByBuyerOrSellerCreatedDateDesc(any());
    assertAll(
        () -> assertThat(responses.size()).isEqualTo(2),
        () -> assertThat(responses.get(0).getPostTitle()).isEqualTo(post.getTitle()),
        () -> assertThat(responses.get(0).getPostBookThumbnail()).isEqualTo(
            post.getBook().getBookThumbnail()),
        () -> assertThat(responses.get(0).getOpponentIdentity()).isEqualTo(seller.getIdentity()),
        () -> assertThat(responses.get(0).isOpponentLeave()).isEqualTo(false),
        () -> assertThat(responses.get(1).getPostTitle()).isEqualTo(post.getTitle()),
        () -> assertThat(responses.get(1).getPostBookThumbnail()).isEqualTo(
            post.getBook().getBookThumbnail()),
        () -> assertThat(responses.get(1).getOpponentIdentity()).isEqualTo(seller.getIdentity()),
        () -> assertThat(responses.get(1).isOpponentLeave()).isEqualTo(true)
    );
  }

  /*
   buyer : 로그인한 유저
   seller : 상대방
  */
  @DisplayName("상대방이 나가지 않은 채팅방을 삭제한다.")
  @Test
  void deleteChatRoom_loginUser_success() {
    ChatRoom chatRoom = ChatRoom.create(post, buyer, seller);

    when(chatRoomRepository.findById(any())).thenReturn(of(chatRoom));

    chatRoomService.deleteChatRoom(buyer, 1L);

    verify(chatRoomRepository).findById(any());
    verify(chatRoomRepository, never()).deleteById(any());
    assertAll(
        () -> assertThat(chatRoom.isBuyerLeave()).isEqualTo(true),
        () -> assertThat(chatRoom.isSellerLeave()).isEqualTo(false)
    );
  }

  @DisplayName("상대방이 나간 채팅방을 삭제한다.")
  @Test
  void deleteChatRoom_both_success() {
    ChatRoom chatRoom = ChatRoom.create(post, buyer, seller);
    chatRoom.leaveChatRoom(seller);

    when(chatRoomRepository.findById(any())).thenReturn(of(chatRoom));

    chatRoomService.deleteChatRoom(buyer, 1L);

    verify(chatRoomRepository).findById(any());
    verify(chatRoomRepository).deleteById(any());
    assertAll(
        () -> assertThat(chatRoom.isBuyerLeave()).isEqualTo(true),
        () -> assertThat(chatRoom.isSellerLeave()).isEqualTo(true)
    );
  }
}
