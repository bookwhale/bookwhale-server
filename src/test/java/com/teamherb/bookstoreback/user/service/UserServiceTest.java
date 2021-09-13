package com.teamherb.bookstoreback.user.service;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.basket.domain.Basket;
import com.teamherb.bookstoreback.basket.dto.BasketResponse;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Book;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private PostRepository postRepository;

  @Mock
  private InterestRepository interestRepository;

  UserService userService;

  User user;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, passwordEncoder, postRepository,
        interestRepository);

    user = User.builder()
        .identity("highright96")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .address("서울")
        .build();
  }

  @DisplayName("회원가입을 한다.")
  @Test
  void createUser_success() {
    when(userRepository.existsByIdentity(any())).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("1234");
    when(userRepository.save(any())).thenReturn(user);

    userService.createUser(new SignUpRequest());

    verify(userRepository).existsByIdentity(any());
    verify(passwordEncoder).encode(any());
    verify(userRepository).save(any());
  }

  @DisplayName("회원가입을 할 때 중복된 아이디면 예외가 발생한다.")
  @Test
  void createUser_duplicatedIdentity_failure() {
    when(userRepository.existsByIdentity(any())).thenReturn(true);
    assertThatThrownBy(() -> userService.createUser(new SignUpRequest())).
        isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.DUPLICATED_USER_IDENTITY.getMessage());
  }

  @DisplayName("내 정보를 수정한다.")
  @Test
  void updateMyInfo_success() {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        .name("주호세")
        .phoneNumber("010-0000-0000")
        .address("경기")
        .build();

    userService.updateMyInfo(user, userUpdateRequest);

    Assertions.assertAll(
        () -> assertThat(user.getName()).isEqualTo(userUpdateRequest.getName()),
        () -> assertThat(user.getAddress()).isEqualTo(userUpdateRequest.getAddress()),
        () -> assertThat(user.getPhoneNumber()).isEqualTo(userUpdateRequest.getPhoneNumber())
    );
  }

  @Test
  @DisplayName("user2의 관심목록을 조회한다.")
  public void findBaskets() {
    User user2 = User.builder()
        .identity("luckyday")
        .name("김첨지")
        .email("kim@sul.com")
        .phoneNumber("010-1234-1234")
        .address("조선")
        .build();

    Book book = Book.builder()
        .bookThumbnail("설렁탕")
        .bookTitle("설렁탕맛잇게 끊이는 법")
        .build();

    Post post = Post.builder()
        .id(1L)
        .book(book)
        .seller(user2)
        .price("10000")
        .title("설렁탕 비법서 팔아요")
        .postStatus(PostStatus.SALE)
        .build();

    Basket basket = Basket.builder()
        .id(1L)
        .purchaser(user2)
        .post(post)
        .build();

    when(basketRepository.findAllByPurchaserOrderByCreatedDate(any())).thenReturn(
        of(basket));

    List<BasketResponse> baskets = userService.findBaskets(user);
    assertAll(
        () -> assertThat(baskets.get(0).getId()).isEqualTo(
            basket.getId()),
        () -> assertThat(baskets.get(0).getBookTitle()).isEqualTo(
            basket.getPost().getBook().getBookTitle()),
        () -> assertThat(baskets.get(0).getPostTitle()).isEqualTo(
            basket.getPost().getTitle()),
        () -> assertThat(baskets.get(0).getBookThumbnail()).isEqualTo(
            basket.getPost().getBook().getBookThumbnail()),
        () -> assertThat(baskets.get(0).getPostStatus()).isEqualTo(
            basket.getPost().getPostStatus().name()),
        () -> assertThat(baskets.get(0).getSellerIdentity()).isEqualTo(
            basket.getPost().getSeller().getIdentity())

    );
  }

  @Test
  @DisplayName("관심목록을 삭제한다.")
  public void delBasket() {

    Basket basket = Basket.builder()
        .id(1L)
        .build();
    when(basketRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(basket));
    userService.delBasket(1L);
  }


}
