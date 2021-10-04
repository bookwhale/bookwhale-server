package com.teamherb.bookstoreback.common;

import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.mail.SmtpMailSender;
import com.teamherb.bookstoreback.post.domain.Book;
import com.teamherb.bookstoreback.post.domain.Post;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("메일 통합 테스트")
class SmtpMailSenderTest extends AcceptanceTest {

  @Autowired
  SmtpMailSender smtpMailSender;

  @Test
  @Disabled
  @DisplayName("메일을 전송한다.")
  void sendMailTest() {
    Post post = Post.builder()
        .title("이펙티브 자바 팝니다.")
        .book(
            Book.builder()
                .bookIsbn("12345678912")
                .bookTitle("이펙티브 자바")
                .bookAuthor("남상우 이일민")
                .bookPublisher("한국출판사")
                .build())
        .build();
    smtpMailSender.sendCreateChatRoomMailToSeller(post, user, anotherUser);
  }
}