package com.teamherb.bookstoreback.common.utils.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.teamherb.bookstoreback.common.utils.mail.MailService.MailDto;

@DisplayName("메일 통합 테스트")
@SpringBootTest
class MailTest {

    @Autowired
    MailService mailService;

    @Test
    @DisplayName("메일을 전송한다.")
    void sendMailTest() throws Exception {
        MailDto mailDto = MailDto.builder()
                .address("highright96@gmail.com")
                .title("메일 테스트")
                .message("내용 테스트")
                .build();
        mailService.mailSend(mailDto);
    }
}