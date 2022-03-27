package com.bookwhale.chatroom.service;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.article.domain.Book;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.chatroom.domain.ChatRoom;
import com.bookwhale.chatroom.domain.ChatRoomRepository;
import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.message.domain.MessageRepository;
import com.bookwhale.push.dto.PushMessageParams;
import com.bookwhale.push.service.PushService;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.service.UserService;
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
    private ArticleRepository articleRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Mock
    private PushService pushService;

    ChatRoomService chatRoomService;

    User buyer;

    User seller;

    Article article;

    @BeforeEach
    void setUp() {
        chatRoomService = new ChatRoomService(chatRoomRepository, articleRepository, messageRepository,
            userService, pushService);
        buyer = User.builder()
            .id(1L)
            .nickname("남상우")
            .email("highright96@email.com")
            .deviceToken("testDeviceToken1")
            .build();

        seller = User.builder()
            .id(2L)
            .nickname("hose12")
            .email("hose12@email.com")
            .deviceToken("testDeviceToken2")
            .build();

        article = Article.create(seller,
            Article.builder()
                .book(Book.builder()
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
                .bookStatus(BookStatus.valueOf("BEST"))
                .price("5000")
                .build()
        );
    }

    @DisplayName("채팅방을 생성하고 판매자와 구매(요청)자에게 push를 보낸다.")
    @Test
    void createArticle() throws Exception {
        ChatRoom chatRoom = ChatRoom.create(article, buyer, seller);

        when(userService.findUserByEmail(any(String.class)))
            .thenReturn(buyer);
        when(userService.findByUserId(any()))
            .thenReturn(of(seller));
        when(articleRepository.findById(any())).thenReturn(of(article));
        when(chatRoomRepository.save(any())).thenReturn(chatRoom);

        chatRoomService.createChatRoom(buyer,
            ChatRoomCreateRequest.builder().sellerId(1L).articleId(1L).build());

        verify(userService).findByUserId(any());
        verify(articleRepository).findById(any());
        verify(chatRoomRepository).save(any());
        verify(pushService, times(2)).sendMessageFromFCM(any(PushMessageParams.class));
    }

    @DisplayName("채팅방을 생성할 때 게시글 상태가 판매완료이면 예외가 발생한다.")
    @Test
    void createArticle_articleStatus_soldOut_failure() {
        article.updateArticleStatus(ArticleStatus.SOLD_OUT.toString());

        when(userService.findUserByEmail(any(String.class)))
            .thenReturn(buyer);
        when(userService.findByUserId(any()))
            .thenReturn(of(seller));
        when(articleRepository.findById(any())).thenReturn(of(article));

        assertThatThrownBy(() -> chatRoomService.createChatRoom(buyer,
            ChatRoomCreateRequest.builder().sellerId(1L).articleId(1L).build()))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_ARTICLE_STATUS.getMessage());
    }

    @DisplayName("채팅방을 생성할 때 게시글 상태가 예약중이면 예외가 발생한다.")
    @Test
    void createArticle_articleStatus_reserved_failure() {
        article.updateArticleStatus(ArticleStatus.RESERVED.toString());

        when(userService.findUserByEmail(any(String.class)))
            .thenReturn(buyer);
        when(userService.findByUserId(any()))
            .thenReturn(of(seller));
        when(articleRepository.findById(any())).thenReturn(of(article));

        assertThatThrownBy(() -> chatRoomService.createChatRoom(buyer,
            ChatRoomCreateRequest.builder().sellerId(1L).articleId(1L).build()))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_ARTICLE_STATUS.getMessage());
    }

    /*
     buyer : 로그인한 유저
     seller : 상대방
    */
    @DisplayName("채팅방들을 조회한다.")
    @Test
    void findChatRooms() {
        // 로그인한 유저와 상대방 모두 존재하는 채팅방
        ChatRoom chatRoom = ChatRoom.create(article, buyer, seller);

        // 로그인한 유저만 존재하는 채팅방
        ChatRoom chatRoomOpponentDelete = ChatRoom.create(article, buyer, seller);
        chatRoomOpponentDelete.deleteChatRoom(seller);

        // 상대방만 존재하는 채팅방
        ChatRoom chatRoomUserDelete = ChatRoom.create(article, buyer, seller);
        chatRoomUserDelete.deleteChatRoom(buyer);

        List<ChatRoom> rooms = List.of(chatRoom, chatRoomOpponentDelete, chatRoomUserDelete);

        when(chatRoomRepository.findAllByBuyerOrSellerCreatedDateDesc(any())).thenReturn(rooms);
        when(userService.findUserByEmail(any(String.class)))
            .thenReturn(buyer);

        List<ChatRoomResponse> responses = chatRoomService.findChatRooms(buyer);

        verify(chatRoomRepository).findAllByBuyerOrSellerCreatedDateDesc(any());
        assertAll(
            () -> assertThat(responses.size()).isEqualTo(2),
            () -> assertThat(responses.get(0).getArticleTitle()).isEqualTo(article.getTitle()),
            () -> assertThat(responses.get(0).getArticleImage()).isEqualTo(
                article.getImages().getFirstImageUrl()),
            () -> assertThat(responses.get(0).getOpponentIdentity()).isEqualTo(
                seller.getNickname()),
            () -> assertThat(responses.get(0).isOpponentDelete()).isEqualTo(false),
            () -> assertThat(responses.get(1).getArticleTitle()).isEqualTo(article.getTitle()),
            () -> assertThat(responses.get(1).getArticleImage()).isEqualTo(
                article.getImages().getFirstImageUrl()),
            () -> assertThat(responses.get(1).getOpponentIdentity()).isEqualTo(
                seller.getNickname()),
            () -> assertThat(responses.get(1).isOpponentDelete()).isEqualTo(true)
        );
    }

    /*
     buyer : 로그인한 유저
     seller : 상대방
    */
    @DisplayName("상대방이 나가지 않은 채팅방을 삭제한다.")
    @Test
    void deleteChatRoom_loginUser_success() {
        ChatRoom chatRoom = ChatRoom.create(article, buyer, seller);

        when(chatRoomRepository.findById(any())).thenReturn(of(chatRoom));
        when(userService.findUserByEmail(any(String.class)))
            .thenReturn(buyer);

        chatRoomService.deleteChatRoom(buyer, 1L);

        verify(chatRoomRepository).findById(any());
        verify(chatRoomRepository, never()).deleteById(any());
        assertAll(
            () -> assertThat(chatRoom.isBuyerDelete()).isEqualTo(true),
            () -> assertThat(chatRoom.isSellerDelete()).isEqualTo(false)
        );
    }

    @DisplayName("상대방이 나간 채팅방을 삭제한다.")
    @Test
    void deleteChatRoom_both_success() {
        ChatRoom chatRoom = ChatRoom.create(article, buyer, seller);
        chatRoom.deleteChatRoom(seller);

        when(chatRoomRepository.findById(any())).thenReturn(of(chatRoom));
        when(userService.findUserByEmail(any(String.class)))
            .thenReturn(buyer);

        chatRoomService.deleteChatRoom(buyer, 1L);

        verify(chatRoomRepository).findById(any());
        verify(chatRoomRepository).deleteById(any());
        assertAll(
            () -> assertThat(chatRoom.isBuyerDelete()).isEqualTo(true),
            () -> assertThat(chatRoom.isSellerDelete()).isEqualTo(true)
        );
    }
}
