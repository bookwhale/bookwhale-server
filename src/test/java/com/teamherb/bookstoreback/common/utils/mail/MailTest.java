package com.teamherb.bookstoreback.common.utils.mail;

import static com.teamherb.bookstoreback.common.utils.mail.MailUtil.MailDto;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Book;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("메일 통합 테스트")
@SpringBootTest
class MailTest {

  @Autowired
  MailUtil mailUtil;

  @Autowired
  PostRepository postRepository;

  @Autowired
  UserRepository userRepository;

  User user;

  @BeforeEach
  void setUp() {

    user = User.builder()
        .id(1L)
        .identity("hose123")
        .password("1234")
        .name("남상우")
        .email("hose0728@naver.com")
        .phoneNumber("010-1234-1234")
        .build();
  }

  @Test
  @Disabled
  @DisplayName("메일을 전송한다.")
  void sendMailTest() throws Exception {
    MailDto mailDto = MailDto.builder()
        .address("hose0728@naver.com")
        .title("메일 테스트")
        .message("내용 테스트")
        .build();
    mailUtil.mailSend(mailDto);
  }

  @DisplayName("구매요청이 들어오면 메일을 보낸다.")
  @Test
  @Disabled
  @Transactional
  void sendPurchaseRequestEmail() throws Exception {

    Book book = Book.builder()
        .bookTitle("설렁탕 비법서")
        .build();

    Post post = Post.builder()
        .title("설렁탕 비법서 팔아요")
        .seller(user)
        .book(book)
        .bookStatus(BookStatus.UPPER)
        .postStatus(PostStatus.SALE)
        .price("1000")
        .description("테스트")
        .build();
    userRepository.save(user);
    postRepository.save(post);
    mailUtil.sendPurchaseRequestEmail(user, 1L);

  }

  @DisplayName("존재하지 않는 게시글에 대해 구매요청이 오면 예외가 발생한다.")
  @Test
  @Disabled
  @Transactional
  void sendPurchaseRequestEmail_failure() {

    assertThatThrownBy(() -> mailUtil.sendPurchaseRequestEmail(user, 2L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.NOT_EXIST_POST_ID.getMessage());
  }

  @DisplayName("예약중인 게시글에 대해 구매요청이 오면 예외가 발생한다.")
  @Test
  @Disabled
  void sendPurchaseRequestToReservedPost() {
    Book book = Book.builder()
        .bookTitle("설렁탕 비법서")
        .build();

    Post post = Post.builder()
        .id(1L)
        .title("설렁탕 비법서 팔아요")
        .seller(user)
        .book(book)
        .postStatus(PostStatus.RESERVED)
        .build();
    userRepository.save(user);
    postRepository.save(post);
    assertThatThrownBy(() -> mailUtil.sendPurchaseRequestEmail(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_STATUS.getMessage());
  }

}