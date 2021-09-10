package com.teamherb.bookstoreback.post.service;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.image.domain.ImageRepository;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.StatusChangeRequest;
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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 단위 테스트(Service)")
public class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private FileStoreUtil fileStoreUtil;

  PostService postService;

  PostRequest postRequest;

  User user;

  @BeforeEach
  void setUp() {
    postService = new PostService(postRepository, imageRepository, fileStoreUtil);

    AccountRequest accountRequest = AccountRequest.builder()
        .accountBank("국민은행")
        .accountOwner("남상우")
        .accountNumber("123-1234-12345")
        .build();

    BookRequest bookRequest = BookRequest.builder()
        .bookSummary("설명")
        .bookPubDate("2021-12-12")
        .bookIsbn("12398128745902")
        .bookListPrice("10000")
        .bookThumbnail("썸네일")
        .bookTitle("책 제목")
        .bookPublisher("출판사")
        .bookAuthor("작가")
        .build();

    postRequest = PostRequest.builder()
        .accountRequest(accountRequest)
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
        .address("서울")
        .build();
  }

  @DisplayName("게시글을 등록한다.")
  @Test
  void createPost() {
    Post post = Post.create(user, postRequest);
    List<String> uploadFilePaths = List.of("image1", "image2");
    List<Image> images = Image.createPostImage(post, uploadFilePaths);

    when(postRepository.save(any())).thenReturn(post);
    when(fileStoreUtil.storeFiles(any())).thenReturn(uploadFilePaths);
    when(imageRepository.saveAll(any())).thenReturn(images);

    postService.createPost(user, postRequest,
        List.of(new MockMultipartFile("images", "image".getBytes(StandardCharsets.UTF_8))));

    verify(postRepository).save(any());
    verify(fileStoreUtil).storeFiles(any());
    verify(imageRepository).saveAll(any());
  }

  @DisplayName("게시글을 상세 조회한다.")
  @Test
  void findPost_success() {
    Post post = Post.create(user, postRequest);
    List<String> uploadFilePaths = List.of("image1", "image2");
    List<Image> images = Image.createPostImage(post, uploadFilePaths);

    when(postRepository.findById(any())).thenReturn(ofNullable(post));
    when(imageRepository.findAllByPost(any())).thenReturn(images);

    PostResponse response = postService.findPost(user, 1L);

    verify(postRepository).findById(any());
    verify(imageRepository).findAllByPost(any());
    assertAll(
        () -> assertThat(response.getTitle()).isEqualTo(postRequest.getTitle()),
        () -> assertThat(response.isMyPost()).isEqualTo(true),
        () -> assertThat(response.getAccountResponse().getAccountBank()).isEqualTo(
            postRequest.getAccountRequest().getAccountBank()),
        () -> assertThat(response.getBookResponse().getBookIsbn()).isEqualTo(
            postRequest.getBookRequest().getBookIsbn())
    );
  }

  @DisplayName("잘못된 게시글 ID로 상세 조회하면 예외가 발생한다.")
  @Test
  void findPost_invalid_postId_failure() {
    when(postRepository.findById(any())).thenReturn(empty());

    assertThatThrownBy(() -> postService.findPost(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_POST_ID.getMessage());
  }

  @DisplayName("게시글의 상태를 변경한다.")
  @Test
  void changePostStatus() {
    Post post = Post.builder()
        .id(1L)
        .postStatus(PostStatus.SALE)
        .build();
    StatusChangeRequest req = StatusChangeRequest.builder()
        .id(1L)
        .status("COMPLETE")
        .build();
    when(postRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(post));
    postService.changeStatus(req);

    assert post != null;
    assertThat(post.getPostStatus().name()).isEqualTo(PostStatus.COMPLETE.name());
  }

}
