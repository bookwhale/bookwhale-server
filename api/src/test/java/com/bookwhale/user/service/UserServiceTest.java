package com.bookwhale.user.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.like.domain.Like;
import com.bookwhale.like.domain.likeRepository;
import com.bookwhale.post.domain.Post;
import com.bookwhale.post.domain.PostRepository;
import com.bookwhale.post.dto.BookRequest;
import com.bookwhale.post.dto.PostRequest;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import com.bookwhale.user.dto.*;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private FileUploader fileUploader;

  @Mock
  private PostRepository postRepository;

  @Mock
  private likeRepository likeRepository;

  UserService userService;
  LikeService likeService;

  User user;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, passwordEncoder, fileUploader, postRepository);
    likeService = new LikeService(likeRepository, userService);

    user = User.builder()
        .id(1L)
        .identity("highright96")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();
  }

  @DisplayName("회원가입을 한다.")
  @Test
  void createUser_success() {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("highright96")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();

    when(userRepository.existsByIdentity(any())).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn(signUpRequest.getPassword());
    when(userRepository.save(any())).thenReturn(user);

    userService.createUser(signUpRequest);

    verify(userRepository).existsByIdentity(any());
    verify(passwordEncoder).encode(any());
    verify(userRepository).save(any());
  }

  @DisplayName("회원가입을 할 때 중복된 아이디면 예외가 발생한다.")
  @Test
  void createUser_duplicatedIdentity_failure() {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("highright96")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();

    when(userRepository.existsByIdentity(any())).thenReturn(true);
    assertThatThrownBy(() -> userService.createUser(signUpRequest)).
        isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.DUPLICATED_USER_IDENTITY.getMessage());
  }

  @DisplayName("내 정보를 수정한다.")
  @Test
  void updateMyInfo_success() {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        .name("주호세")
        .phoneNumber("010-0000-0000")
        .email("hose@email.com")
        .build();

    userService.updateMyInfo(user, userUpdateRequest);

    Assertions.assertAll(
        () -> assertThat(user.getName()).isEqualTo(userUpdateRequest.getName()),
        () -> assertThat(user.getEmail()).isEqualTo(userUpdateRequest.getEmail()),
        () -> assertThat(user.getPhoneNumber()).isEqualTo(userUpdateRequest.getPhoneNumber())
    );
  }

  @DisplayName("비밀번호를 수정한다.")
  @Test
  void updatePassword_success() {
    PasswordUpdateRequest request = new PasswordUpdateRequest("1234", "12345");

    when(passwordEncoder.matches(any(), any())).thenReturn(true);
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    userService.updatePassword(user, request);

    assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(request.getNewPassword()));
  }

  @DisplayName("비밀번호를 수정을 할 때 기존 비밀번호가 틀리면 예외가 발생한다.")
  @Test
  void updatePassword_failure() {
    PasswordUpdateRequest req = new PasswordUpdateRequest("invalidPassword", "12345");

    when(passwordEncoder.matches(any(), any())).thenReturn(false);

    assertThatThrownBy(() -> userService.updatePassword(user, req))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_USER_PASSWORD.getMessage());
  }

  @DisplayName("프로필 사진을 업로드한다.")
  @Test
  void uploadProfileImage_success() {
    MockMultipartFile image = new MockMultipartFile("profileImage", "profileImage.jpg",
        ContentType.IMAGE_JPEG.getMimeType(),
        "프로필 이미지 입니다.".getBytes());
    String uploadedImage = "uploadImage";

    when(fileUploader.uploadFile(any())).thenReturn(uploadedImage);

    ProfileResponse profileResponse = userService.uploadProfileImage(user, image);

    assertThat(profileResponse.getProfileImage()).isEqualTo(uploadedImage);
  }

  @DisplayName("프로필 사진을 삭제한다.")
  @Test
  void deleteProfileImage_success() {
    userService.deleteProfileImage(user);
    assertThat(user.getProfileImage()).isEqualTo(null);
  }

  @DisplayName("관심목록을 조회한다.")
  @Test
  void findLikes_success() {
    BookRequest bookRequest = BookRequest.builder()
        .bookSummary("설명")
        .bookPubDate("2021-12-12")
        .bookIsbn("12398128745902")
        .bookListPrice("10000")
        .bookThumbnail("썸네일")
        .bookTitle("토비의 스프링")
        .bookPublisher("출판사")
        .bookAuthor("이일민")
        .build();

    PostRequest postRequest = PostRequest.builder()
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();
    Post post = Post.create(user, postRequest.toEntity());
    post.setCreatedDate(LocalDateTime.now());

    when(likeRepository.findAllByUser(any())).thenReturn(List.of(Like.create(user, post)));

    List<LikeResponse> responses = likeService.findAllLikes(user);

    verify(likeRepository).findAllByUser(any());
    Assertions.assertAll(
        () -> assertThat(responses.size()).isEqualTo(1),
        () -> org.assertj.core.api.Assertions.assertThat(responses.get(0).getPostsResponse().getPostImage()).isNull(),
        () -> org.assertj.core.api.Assertions.assertThat(responses.get(0).getPostsResponse().getBookTitle()).isEqualTo(
            bookRequest.getBookTitle()),
        () -> org.assertj.core.api.Assertions.assertThat(responses.get(0).getPostsResponse().getPostTitle()).isEqualTo(
            postRequest.getTitle()),
        () -> org.assertj.core.api.Assertions.assertThat(responses.get(0).getPostsResponse().getPostPrice()).isEqualTo(
            postRequest.getPrice())
    );
  }

  @DisplayName("관심목록에 추가한다.")
  @Test
  void addLike_success() {
    BookRequest bookRequest = BookRequest.builder()
        .bookSummary("설명")
        .bookPubDate("2021-12-12")
        .bookIsbn("12398128745902")
        .bookListPrice("10000")
        .bookThumbnail("썸네일")
        .bookTitle("토비의 스프링")
        .bookPublisher("출판사")
        .bookAuthor("이일민")
        .build();

    PostRequest postRequest = PostRequest.builder()
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();
    Post post = Post.create(user, postRequest.toEntity());

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
    when(likeRepository.save(any())).thenReturn(Like.create(user, post));

    likeService.addLike(user, new LikeRequest(1L));

    verify(postRepository).findById(any());
    verify(likeRepository).save(any());
  }

  @DisplayName("잘못된 post_id 로 관심목록에 추가하면 예외가 발생한다.")
  @Test
  void addLike_invalidPostId_failure() {
    when(postRepository.findById(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> likeService.addLike(user, new LikeRequest(1L)))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("관심목록을 삭제한다.")
  @Test
  void deleteLike_success() {
    BookRequest bookRequest = BookRequest.builder()
        .bookSummary("설명")
        .bookPubDate("2021-12-12")
        .bookIsbn("12398128745902")
        .bookListPrice("10000")
        .bookThumbnail("썸네일")
        .bookTitle("토비의 스프링")
        .bookPublisher("출판사")
        .bookAuthor("이일민")
        .build();

    PostRequest postRequest = PostRequest.builder()
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();
    Post post = Post.create(user, postRequest.toEntity());

    when(likeRepository.findById(any())).thenReturn(Optional.of(Like.create(user, post)));
    doNothing().when(likeRepository).delete(any());

    likeService.deleteLike(user, 1L);

    verify(likeRepository).findById(any());
  }

  @DisplayName("권한이 없는 유저가 관심목록을 삭제하면 예외가 발생한다.")
  @Test
  void deleteLike_isNotMyLike_failure() {
    BookRequest bookRequest = BookRequest.builder()
        .bookSummary("설명")
        .bookPubDate("2021-12-12")
        .bookIsbn("12398128745902")
        .bookListPrice("10000")
        .bookThumbnail("썸네일")
        .bookTitle("토비의 스프링")
        .bookPublisher("출판사")
        .bookAuthor("이일민")
        .build();

    PostRequest postRequest = PostRequest.builder()
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();
    Post post = Post.create(user, postRequest.toEntity());
    User otherUser = User.builder().id(2L).build();

    when(likeRepository.findById(any())).thenReturn(
        Optional.of(Like.create(otherUser, post)));

    assertThatThrownBy(() -> likeService.deleteLike(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
  }

  @DisplayName("잘못된 like_id 로 관심목록을 삭제하면 예외가 발생한다.")
  @Test
  void deleteLike_invalidLikeId_failure() {
    when(likeRepository.findById(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> likeService.deleteLike(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_LIKE_ID.getMessage());
  }
}
