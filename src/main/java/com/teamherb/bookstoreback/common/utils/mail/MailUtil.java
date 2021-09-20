package com.teamherb.bookstoreback.common.utils.mail;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {

  private final JavaMailSender mailSender;

  private final PostRepository postRepository;

  @Value("${spring.mail.username}")
  private String email;

  public void sendPurchaseRequestEmail(User user, Long postId) throws Exception {
    Post getPost = validatePostAndGetPost(postId);
    getPost.validPurchaseRequest();
    MailDto sendMailDto = MailDto.builder()
        .address(user.getEmail())
        .title(String.format("%s 게시글에 올리신 책에 대한 구매요청이 왔습니다.", getPost.getTitle()))
        .message(String.format("%s 사용자가 %s 게시글에 올려진 책에 대한 구매요청을 하였습니다.", user.getIdentity(),
            getPost.getTitle()))
        .build();
    mailSend(sendMailDto);
  }

  private Post validatePostAndGetPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST_ID));
  }

  public void mailSend(MailDto MailDto) throws Exception {

    MailHandler mailHandler = new MailHandler(mailSender);

    mailHandler.setTo(MailDto.getAddress());
    mailHandler.setFrom(email);
    mailHandler.setSubject(MailDto.getTitle());

    String htmlContent = "<p>" + MailDto.getMessage() + "<p> <img src='cid:teamherb'>";
    mailHandler.setText(htmlContent, true);

    // 첨부 파일
    //mailHandler.setAttach("newTest.txt", "static/originTest.txt");

    // 이미지 삽입
    mailHandler.setInline("teamherb", "static/teamherb.png");

    mailHandler.send();
  }

  @Data
  @NoArgsConstructor
  public static class MailDto {

    private String address;
    private String title;
    private String message;

    @Builder
    public MailDto(String address, String title, String message) {
      this.address = address;
      this.title = title;
      this.message = message;
    }
  }
}
