package com.teamherb.bookstoreback.post.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.image.domain.ImageRepository;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.user.domain.User;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

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
        List<Image> postImages = Image.createPostImage(post, uploadFilePaths);

        when(postRepository.save(any())).thenReturn(post);
        when(fileStoreUtil.storeFiles(any())).thenReturn(uploadFilePaths);
        when(imageRepository.saveAll(any())).thenReturn(postImages);

        postService.createPost(user, postRequest,
            List.of(new MockMultipartFile("images", "image".getBytes(StandardCharsets.UTF_8))));

        verify(postRepository).save(any());
        verify(fileStoreUtil).storeFiles(any());
        verify(imageRepository).saveAll(any());
    }
}
