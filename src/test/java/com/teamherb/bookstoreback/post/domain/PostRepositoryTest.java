package com.teamherb.bookstoreback.post.domain;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.config.AppConfig;
import com.teamherb.bookstoreback.post.dto.PostsRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

@DisplayName("게시글 단위 테스트(Repository)")
@Import(AppConfig.class)
@DataJpaTest
public class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  Post tobySpringPost;

  Post hrSpringPost;

  Post effectiveJavaPost;

  @BeforeEach
  void setUp() {
    Book tobySpring = Book.builder()
        .bookIsbn("12345678910")
        .bookTitle("토비의 스프링")
        .bookAuthor("이일민")
        .bookPublisher("허브출판사")
        .build();

    Book hrSpring = Book.builder()
        .bookIsbn("12345678911")
        .bookTitle("상우의 스프링")
        .bookAuthor("남상우")
        .bookPublisher("허브출판사")
        .build();

    Book effectiveJava = Book.builder()
        .bookIsbn("12345678912")
        .bookTitle("이펙티브 자바")
        .bookAuthor("남상우 이일민")
        .bookPublisher("한국출판사")
        .build();

    tobySpringPost = Post.builder()
        .title("토비의 스프링 팝니다.")
        .book(tobySpring)
        .build();

    hrSpringPost = Post.builder()
        .title("상우의 스프링 팝니다.")
        .book(hrSpring)
        .build();

    effectiveJavaPost = Post.builder()
        .title("이펙티브 자바 팝니다.")
        .book(effectiveJava)
        .build();

    postRepository.saveAll(of(tobySpringPost, hrSpringPost, effectiveJavaPost));
  }

  @DisplayName("책 제목에 스프링이 포함된 게시글들을 오름차순으로 찾는다.")
  @Test
  void findAllByPostsReqOrderByCreatedDateDesc_bookTitle() {
    PostsRequest postsRequest = PostsRequest.builder()
        .title("스프링")
        .build();
    PageRequest pageRequest = PageRequest.of(0, 10);

    List<Post> res = postRepository.findAllByPostsReqOrderByCreatedDateDesc(postsRequest,
        pageRequest).getContent();

    Assertions.assertAll(
        () -> assertThat(res.size()).isEqualTo(2),
        () -> assertThat(res.get(0).getBook().getBookTitle()).isEqualTo(
            hrSpringPost.getBook().getBookTitle()),
        () -> assertThat(res.get(1).getBook().getBookTitle()).isEqualTo(
            tobySpringPost.getBook().getBookTitle())
    );
  }

  @DisplayName("책 저자에 남상우가 포함된 게시글들을 오름차순으로 찾는다.")
  @Test
  void findAllByPostsReqOrderByCreatedDateDesc_bookAuthor() {
    PostsRequest postsRequest = PostsRequest.builder()
        .author("남상우")
        .build();
    PageRequest pageRequest = PageRequest.of(0, 10);

    List<Post> res = postRepository.findAllByPostsReqOrderByCreatedDateDesc(postsRequest,
        pageRequest).getContent();

    Assertions.assertAll(
        () -> assertThat(res.size()).isEqualTo(2),
        () -> assertThat(res.get(0).getBook().getBookTitle()).isEqualTo(
            effectiveJavaPost.getBook().getBookTitle()),
        () -> assertThat(res.get(1).getBook().getBookTitle()).isEqualTo(
            hrSpringPost.getBook().getBookTitle())
    );
  }

  @DisplayName("책 출판사에 허브가 포함된 게시글들을 오름차순으로 찾는다.")
  @Test
  void findAllByPostsReqOrderByCreatedDateDesc_bookPublisher() {
    PostsRequest postsRequest = PostsRequest.builder()
        .publisher("허브")
        .build();
    PageRequest pageRequest = PageRequest.of(0, 10);

    List<Post> res = postRepository.findAllByPostsReqOrderByCreatedDateDesc(postsRequest,
        pageRequest).getContent();

    Assertions.assertAll(
        () -> assertThat(res.size()).isEqualTo(2),
        () -> assertThat(res.get(0).getBook().getBookTitle()).isEqualTo(
            hrSpringPost.getBook().getBookTitle()),
        () -> assertThat(res.get(1).getBook().getBookTitle()).isEqualTo(
            tobySpringPost.getBook().getBookTitle())
    );
  }

  @DisplayName("페이징이 옳바르게 작동하는지 확인한다.")
  @Test
  void findAllByPostsReqOrderByCreatedDateDesc_paging() {
    PostsRequest postsRequest = PostsRequest.builder()
        .author("남상우")
        .build();
    PageRequest pageRequest = PageRequest.of(0, 2);

    List<Post> res = postRepository.findAllByPostsReqOrderByCreatedDateDesc(postsRequest,
        pageRequest).getContent();

    Assertions.assertAll(
        () -> assertThat(res.size()).isEqualTo(2),
        () -> assertThat(res.get(0).getBook().getBookTitle()).isEqualTo(
            effectiveJavaPost.getBook().getBookTitle()),
        () -> assertThat(res.get(1).getBook().getBookTitle()).isEqualTo(
            hrSpringPost.getBook().getBookTitle())
    );
  }
}
