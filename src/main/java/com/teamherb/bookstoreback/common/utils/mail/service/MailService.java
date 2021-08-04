package com.teamherb.bookstoreback.common.utils.mail.service;

import com.teamherb.bookstoreback.common.utils.mail.MailHandler;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;

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
