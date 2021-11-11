package com.bookwhale.post.domain;

import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostDomainTest {

  @DisplayName("판매글의 조회수 증가, 관심수 증감 기능 확인")
  @Test
  void increaseOneViewCount() {
    Book tobySpring = Book.builder()
        .bookIsbn("12345678910")
        .bookTitle("토비의 스프링")
        .bookAuthor("이일민")
        .bookPublisher("허브출판사")
        .build();

    Post tobySpringPost = Post.builder()
        .title("토비의 스프링 팝니다.")
        .book(tobySpring)
        .build();

    tobySpringPost.decreaseOneLikeCount();
    tobySpringPost.increaseOneLikeCount();
    tobySpringPost.increaseOneViewCount();

    // 최초 0인 상태에서는 감소하지 않아야 한다.
    MatcherAssert.assertThat(tobySpringPost.getLikeCount(), is(1L));
    MatcherAssert.assertThat(tobySpringPost.getViewCount(), is(1L));
  }
}