package com.bookwhale.post.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.like.domain.likeRepository;
import com.bookwhale.post.domain.BookStatus;
import com.bookwhale.post.domain.Post;
import com.bookwhale.post.domain.PostRepository;
import com.bookwhale.post.domain.PostStatus;
import com.bookwhale.post.dto.*;
import com.bookwhale.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.emptyList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 단위 테스트(Service)")
public class PostServiceTest {

  @Mock
  PostRepository postRepository;

  @Mock
  FileUploader fileUploader;

  @Mock
  likeRepository likeRepository;

  PostService postService;

  PostRequest postRequest;

  User user;

  @BeforeEach
  void setUp() {
    postService = new PostService(postRepository, fileUploader, likeRepository);

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

    postRequest = PostRequest.builder()
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();

    user = User.builder()
        .id(1L)
        .identity("highright96")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();
  }

  @DisplayName("게시글을 등록한다.")
  @Test
  void createPost() {
    Post post = Post.create(user, postRequest.toEntity());
    List<String> images = of("image1", "image2");

    when(fileUploader.uploadFiles(any())).thenReturn(images);
    when(postRepository.save(any())).thenReturn(post);

    postService.createPost(user, postRequest,
        of(new MockMultipartFile("images", "image".getBytes(StandardCharsets.UTF_8))));

    verify(postRepository).save(any());
    verify(fileUploader).uploadFiles(any());
  }

  @DisplayName("나의 게시글을 상세 조회한다. (게시글 이미지 2개)")
  @Test
  void findMyPost_success() {
    Post post = Post.create(user, postRequest.toEntity());
    List<String> images = of("image1", "image2");
    post.getImages().addAll(post, images);
    post.setCreatedDate(LocalDateTime.now());

    when(postRepository.findPostWithSellerById(any())).thenReturn(Optional.of(post));
    when(likeRepository.existsByUserAndPost(any(), any())).thenReturn(true);

    PostResponse response = postService.findPost(user, 1L);

    verify(postRepository).findPostWithSellerById(any());
    assertAll(
        () -> assertThat(response.getTitle()).isEqualTo(postRequest.getTitle()),
        () -> assertThat(response.getPrice()).isEqualTo(postRequest.getPrice()),
        () -> assertThat(response.getDescription()).isEqualTo(postRequest.getDescription()),
        () -> assertThat(response.getPostStatus()).isEqualTo(PostStatus.SALE.getName()),
        () -> assertThat(response.getTitle()).isEqualTo(postRequest.getTitle()),
        () -> assertThat(response.isMyPost()).isEqualTo(true),
        () -> assertThat(response.isMyLike()).isEqualTo(true),
        () -> assertThat(response.getBookStatus()).isEqualTo(
            BookStatus.valueOf(postRequest.getBookStatus()).getName()),
        () -> assertThat(response.getImages().size()).isEqualTo(2),
        () -> assertThat(response.getBookResponse().getBookIsbn()).isEqualTo(
            postRequest.getBookRequest().getBookIsbn()),
        () -> assertThat(response.getBookResponse().getBookAuthor()).isEqualTo(
            postRequest.getBookRequest().getBookAuthor()),
        () -> assertThat(response.getBookResponse().getBookTitle()).isEqualTo(
            postRequest.getBookRequest().getBookTitle()),
        () -> assertThat(response.getBookResponse().getBookPublisher()).isEqualTo(
            postRequest.getBookRequest().getBookPublisher()),
        () -> assertThat(response.getBookResponse().getBookSummary()).isEqualTo(
            postRequest.getBookRequest().getBookSummary()),
        () -> assertThat(response.getBookResponse().getBookThumbnail()).isEqualTo(
            postRequest.getBookRequest().getBookThumbnail()),
        () -> assertThat(response.getBookResponse().getBookPubDate()).isEqualTo(
            postRequest.getBookRequest().getBookPubDate()),
        () -> assertThat(response.getBookResponse().getBookListPrice()).isEqualTo(
            postRequest.getBookRequest().getBookListPrice())
    );
  }

  @DisplayName("다른 유저의 게시글을 상세 조회한다. (게시글 이미지 0개)")
  @Test
  void findNotMyPost_success() {
    User otherUser = User.builder().id(2L).build();
    Post post = Post.create(user, postRequest.toEntity());
    post.setCreatedDate(LocalDateTime.now());

    when(postRepository.findPostWithSellerById(any())).thenReturn(ofNullable(post));

    PostResponse response = postService.findPost(otherUser, 1L);

    verify(postRepository).findPostWithSellerById(any());
    assertAll(
        () -> assertThat(response.isMyPost()).isEqualTo(false),
        () -> assertThat(response.getImages().isEmpty()).isEqualTo(true)
    );
  }

  @DisplayName("잘못된 게시글 ID로 상세 조회하면 예외가 발생한다.")
  @Test
  void findPost_invalidPostId_failure() {
    when(postRepository.findPostWithSellerById(any())).thenReturn(empty());

    assertThatThrownBy(() -> postService.findPost(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("게시글을 수정한다. (게시글 이미지 1개, 삭제하는 이미지 0개, 추가하는 이미지 1개 -> 총 2개)")
  @Test
  void updatePost_One_success() {
    Post post = Post.create(user, postRequest.toEntity());
    post.getImages().addAll(post, of("image1"));

    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("이펙티브 자바")
        .description("이펙티브 자바입니다.")
        .price("15000")
        .bookStatus("BEST")
        .build();

    List<MultipartFile> images = of(
        new MockMultipartFile("images", "image1".getBytes(StandardCharsets.UTF_8))
    );

    when(postRepository.findById(any())).thenReturn(Optional.of(post));
    when(fileUploader.uploadFiles(any())).thenReturn(of("image1"));

    postService.updatePost(user, 1L, request, images);

    verify(postRepository).findById(any());
    verify(fileUploader, never()).deleteFile(any());
    verify(fileUploader).uploadFiles(any());
    assertAll(
        () -> assertThat(post.getTitle()).isEqualTo(request.getTitle()),
        () -> assertThat(post.getPrice()).isEqualTo(request.getPrice()),
        () -> assertThat(post.getDescription()).isEqualTo(request.getDescription()),
        () -> Assertions.assertThat(post.getImages().getSize()).isEqualTo(2),
        () -> assertThat(post.getBookStatus()).isEqualTo(BookStatus.valueOf(request.getBookStatus()))
    );
  }

  @DisplayName("게시글을 수정한다. (게시글 이미지 3개, 삭제하는 이미지 1개, 추가하는 이미지 1개 -> 총 3개)")
  @Test
  void updatePost_Two_success() {
    Post post = Post.create(user, postRequest.toEntity());
    post.getImages().addAll(post, of("image1", "image2", "image3"));

    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("이펙티브 자바")
        .description("이펙티브 자바입니다.")
        .price("15000")
        .bookStatus("BEST")
        .deleteImgUrls(List.of("image2"))
        .build();

    List<MultipartFile> images = of(
        new MockMultipartFile("images", "image4".getBytes(StandardCharsets.UTF_8))
    );

    when(postRepository.findById(any())).thenReturn(Optional.of(post));
    doNothing().when(fileUploader).deleteFiles(anyList());
    when(fileUploader.uploadFiles(any())).thenReturn(of("image4"));

    postService.updatePost(user, 1L, request, images);

    verify(postRepository).findById(any());
    verify(fileUploader).deleteFiles(anyList());
    verify(fileUploader).uploadFiles(any());
    assertAll(
        () -> assertThat(post.getTitle()).isEqualTo(request.getTitle()),
        () -> assertThat(post.getPrice()).isEqualTo(request.getPrice()),
        () -> assertThat(post.getDescription()).isEqualTo(request.getDescription()),
        () -> Assertions.assertThat(post.getImages().getSize()).isEqualTo(3),
        () -> assertThat(post.getBookStatus()).isEqualTo(BookStatus.valueOf(request.getBookStatus()))
    );
  }

  @DisplayName("게시글을 수정한다. (게시글 이미지 3개, 삭제하는 이미지 2개, 추가하는 이미지 0개 -> 총 1개)")
  @Test
  void updatePost_Three_success() {
    Post post = Post.create(user, postRequest.toEntity());
    post.getImages().addAll(post, of("image1", "image2", "image3"));

    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("이펙티브 자바")
        .description("이펙티브 자바입니다.")
        .price("15000")
        .bookStatus("BEST")
        .deleteImgUrls(List.of("image2", "image3"))
        .build();

    when(postRepository.findById(any())).thenReturn(Optional.of(post));
    doNothing().when(fileUploader).deleteFiles(anyList());

    postService.updatePost(user, 1L, request, emptyList());

    verify(postRepository).findById(any());
    verify(fileUploader).deleteFiles(anyList());
    verify(fileUploader, never()).uploadFiles(any());
    assertAll(
        () -> assertThat(post.getTitle()).isEqualTo(request.getTitle()),
        () -> assertThat(post.getPrice()).isEqualTo(request.getPrice()),
        () -> assertThat(post.getDescription()).isEqualTo(request.getDescription()),
        () -> Assertions.assertThat(post.getImages().getSize()).isEqualTo(1),
        () -> assertThat(post.getBookStatus()).isEqualTo(BookStatus.valueOf(request.getBookStatus()))
    );
  }

  @DisplayName("잘못된 post_id로 게시글을 수정하면 예외가 발생한다.")
  @Test
  void updatePost_invalidPostId_failure() {
    PostUpdateRequest request = PostUpdateRequest.builder().build();
    List<MultipartFile> images = emptyList();

    when(postRepository.findById(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> postService.updatePost(user, 1L, request, images))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("권한이 없는 유저가 게시글을 수정하면 예외가 발생한다.")
  @Test
  void updatePost_invalidUser_failure() {
    User otherUser = User.builder().id(2L).build();
    Post post = Post.create(otherUser, postRequest.toEntity());
    PostUpdateRequest request = PostUpdateRequest.builder().build();
    List<MultipartFile> images = emptyList();

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

    assertThatThrownBy(() -> postService.updatePost(user, 1L, request, images))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
  }

  @DisplayName("게시글 상태를 변경한다.")
  @Test
  void updatePostStatus_success() {
    Post post = Post.create(user, postRequest.toEntity());
    PostStatusUpdateRequest request = new PostStatusUpdateRequest(PostStatus.SOLD_OUT.toString());

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

    postService.updatePostStatus(user, 1L, request);

    verify(postRepository).findById(any());
    assertThat(post.getPostStatus()).isEqualTo(PostStatus.valueOf(request.getPostStatus()));
  }

  @DisplayName("잘못된 post_id 로 게시글 상태를 변경하면 예외가 발생한다.")
  @Test
  void updatePostStatus_invalidPostId_failure() {
    PostStatusUpdateRequest request = new PostStatusUpdateRequest(PostStatus.SOLD_OUT.toString());

    when(postRepository.findById(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> postService.updatePostStatus(user, 1L, request))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("권한이 없는 유저가 게시글 상태를 변경하면 예외가 발생한다.")
  @Test
  void updatePostStatus_invalidUser_failure() {
    Post post = Post.create(user, postRequest.toEntity());
    PostStatusUpdateRequest request = new PostStatusUpdateRequest(PostStatus.SOLD_OUT.toString());
    User otherUser = User.builder().id(2L).build();

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

    assertThatThrownBy(() -> postService.updatePostStatus(otherUser, 1L, request))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
  }
}
