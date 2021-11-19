package com.bookwhale.common.mail;

import static java.lang.String.format;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.post.domain.Post;
import com.bookwhale.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpMailSender {

    private final JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String email;

    public void sendChatRoomCreationMail(Post post, User seller, User buyer) {
        String subject = format("%s 게시글에 대한 새로운 채팅방이 개설되었습니다.", post.getTitle());
        String content = format("[%s]님이 [%s] 게시글에 대한 새로운 채팅방을 개설했습니다. 채팅을 확인해주세요.",
            buyer.getIdentity(), post.getTitle());
        mailSend(email, seller.getEmail(), subject, content);
    }

    public void mailSend(String from, String to, String subject, String content) {
        try {
            MailHandler mailHandler = new MailHandler(sender);
            mailHandler.setFrom(from);
            mailHandler.setTo(to);
            mailHandler.setSubject(subject);
            mailHandler.setText("<p>" + content + "</p>", true);
            mailHandler.send();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAILED_TO_SEND_MAIL);
        }
    }
}
