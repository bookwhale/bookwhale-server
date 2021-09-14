package com.teamherb.bookstoreback.post.service;

import static java.util.List.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.emptyList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.Interest.domain.InterestRepository;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.image.domain.ImageRepository;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.PostStatusUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.user.domain.User;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 단위 테스트(Service)")
public class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private FileStoreUtil fileStoreUtil;

  @Mock
  private InterestRepository interestRepository;

  PostService postService;

  PostRequest postRequest;

  User user;

  @BeforeEach
  void setUp() {
    postService = new PostService(postRepository, imageRepository, fileStoreUtil,
        interestRepository);

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
    Post post = Post.create(user, postRequest);
    List<String> uploadFilePaths = of("image1", "image2");
    List<Image> images = Image.createPostImage(post, uploadFilePaths);

    when(postRepository.save(any())).thenReturn(post);
    when(fileStoreUtil.storeFiles(any())).thenReturn(uploadFilePaths);
    when(imageRepository.saveAll(any())).thenReturn(images);

    postService.createPost(user, postRequest,
        of(new MockMultipartFile("images", "image".getBytes(StandardCharsets.UTF_8))));

    verify(postRepository).save(any());
    verify(fileStoreUtil).storeFiles(any());
    verify(imageRepository).saveAll(any());
  }

  @DisplayName("나의 게시글을 상세 조회한다.")
  @Test
  void findMyPost_success() {
    Post post = Post.create(user, postRequest);
    List<String> uploadFilePaths = of("image1", "image2");
    List<Image> images = Image.createPostImage(post, uploadFilePaths);

    when(postRepository.findWithSellerById(any())).thenReturn(ofNullable(post));
    when(imageRepository.findAllByPost(any())).thenReturn(images);
    when(interestRepository.existsByUserAndPost(any(), any())).thenReturn(true);

    PostResponse response = postService.findPost(user, 1L);

    verify(postRepository).findWithSellerById(any());
    verify(imageRepository).findAllByPost(any());
    assertAll(
        () -> assertThat(response.getTitle()).isEqualTo(postRequest.getTitle()),
        () -> assertThat(response.getPrice()).isEqualTo(postRequest.getPrice()),
        () -> assertThat(response.getDescription()).isEqualTo(postRequest.getDescription()),
        () -> assertThat(response.getPostStatus()).isEqualTo(PostStatus.SALE),
        () -> assertThat(response.getTitle()).isEqualTo(postRequest.getTitle()),
        () -> assertThat(response.isMyPost()).isEqualTo(true),
        () -> assertThat(response.isMyInterest()).isEqualTo(true),
        () -> assertThat(response.getBookStatus()).isEqualTo(
            BookStatus.valueOf(postRequest.getBookStatus())),
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

  @DisplayName("다른 유저의 게시글을 상세 조회한다.")
  @Test
  void findNotMyPost_success() {
    User otherUser = User.builder()
        .id(2L)
        .identity("hose")
        .name("주호세")
        .email("hose@email.com")
        .phoneNumber("010-5678-5678")
        .build();

    Post post = Post.create(user, postRequest);

    when(postRepository.findWithSellerById(any())).thenReturn(ofNullable(post));
    when(imageRepository.findAllByPost(any())).thenReturn(emptyList());

    PostResponse response = postService.findPost(otherUser, 1L);

    verify(postRepository).findWithSellerById(any());
    verify(imageRepository).findAllByPost(any());
    assertThat(response.isMyPost()).isEqualTo(false);
  }

  @DisplayName("잘못된 게시글 ID로 상세 조회하면 예외가 발생한다.")
  @Test
  void findPost_invalidPostId_failure() {
    when(postRepository.findWithSellerById(any())).thenReturn(empty());

    assertThatThrownBy(() -> postService.findPost(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("게시글을 수정한다.")
  @Test
  void updatePost_success() {
    Post post = Post.create(user, postRequest);
    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("이펙티브 자바")
        .description("이펙티브 자바입니다.")
        .price("15000")
        .bookStatus("BEST")
        .build();
    List<MultipartFile> images = of(
        new MockMultipartFile("updateImages", "image1".getBytes(StandardCharsets.UTF_8)),
        new MockMultipartFile("updateImages", "image2".getBytes(StandardCharsets.UTF_8))
    );

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
    when(fileStoreUtil.storeFiles(any())).thenReturn(of("image1", "image2"));
    when(imageRepository.findAllByPost(any())).thenReturn(Image.createPostImage(post, of("image")));
    doNothing().when(imageRepository).deleteAll(any());
    when(imageRepository.saveAll(any())).thenReturn(
        Image.createPostImage(post, of("image1", "image2")));

    postService.updatePost(user, 1L, request, images);

    verify(postRepository).findById(any());
    verify(imageRepository).findAllByPost(any());
    verify(imageRepository).deleteAll(any());
    verify(fileStoreUtil).storeFiles(any());
    verify(imageRepository).saveAll(any());
    assertAll(
        () -> assertThat(post.getTitle()).isEqualTo(request.getTitle()),
        () -> assertThat(post.getPrice()).isEqualTo(request.getPrice()),
        () -> assertThat(post.getDescription()).isEqualTo(request.getDescription()),
        () -> assertThat(post.getBookStatus()).isEqualTo(
            BookStatus.valueOf(request.getBookStatus()))
    );
  }

  @DisplayName("게시글을 수정한다. (빈 이미지 리스트를 보내는 경우)")
  @Test
  void updatePost_emptyImageList_success() {
    Post post = Post.create(user, postRequest);
    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("이펙티브 자바")
        .description("이펙티브 자바입니다.")
        .price("15000")
        .bookStatus("BEST")
        .build();
    List<MultipartFile> images = emptyList();

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
    when(imageRepository.findAllByPost(any())).thenReturn(Image.createPostImage(post, of("image")));
    doNothing().when(imageRepository).deleteAll(any());

    postService.updatePost(user, 1L, request, images);

    verify(postRepository).findById(any());
    verify(imageRepository).findAllByPost(any());
    verify(imageRepository).deleteAll(any());
    verify(fileStoreUtil, never()).storeFiles(any());
    verify(imageRepository, never()).saveAll(any());
  }

  @DisplayName("게시글을 수정한다. (null 을 보내는 경우)")
  @Test
  void updatePost_nullImageList_success() {
    Post post = Post.create(user, postRequest);
    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("이펙티브 자바")
        .description("이펙티브 자바입니다.")
        .price("15000")
        .bookStatus("BEST")
        .build();

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
    when(imageRepository.findAllByPost(any())).thenReturn(Image.createPostImage(post, of("image")));
    doNothing().when(imageRepository).deleteAll(any());

    postService.updatePost(user, 1L, request, null);

    verify(postRepository).findById(any());
    verify(imageRepository).findAllByPost(any());
    verify(imageRepository).deleteAll(any());
    verify(fileStoreUtil, never()).storeFiles(any());
    verify(imageRepository, never()).saveAll(any());
  }

  @DisplayName("잘못된 post_id로 게시글을 수정하면 예외가 발생한다.")
  @Test
  void updatePost_invalidPostId_failure() {
    PostUpdateRequest request = PostUpdateRequest.builder().build();
    List<MultipartFile> images = emptyList();

    when(postRepository.findById(any())).thenReturn(empty());

    assertThatThrownBy(() -> postService.updatePost(user, 1L, request, images))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("권한이 없는 유저가 게시글을 수정하면 예외가 발생한다.")
  @Test
  void updatePost_invalidUser_failure() {
    User otherUser = User.builder().id(2L).build();
    Post post = Post.create(otherUser, postRequest);
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
    Post post = Post.create(user, postRequest);
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
    Post post = Post.create(user, postRequest);
    PostStatusUpdateRequest request = new PostStatusUpdateRequest(PostStatus.SOLD_OUT.toString());
    User otherUser = User.builder().id(2L).build();

    when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

    assertThatThrownBy(() -> postService.updatePostStatus(otherUser, 1L, request))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
  }
}
