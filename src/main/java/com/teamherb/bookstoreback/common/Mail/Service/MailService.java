package com.teamherb.bookstoreback.common.Mail.Service;

import com.teamherb.bookstoreback.common.Mail.domain.Mail;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "hose123@ajou.ac.kr";

    public void mailSend(Mail mail) {
        System.out.println("메일 출력");
        System.out.println("mail.getAddress() = " + mail.getAddress());
        System.out.println("mail.getMessage() = " + mail.getMessage());
        System.out.println("mail.getTitle() = " + mail.getTitle());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getAddress());
        message.setFrom(MailService.FROM_ADDRESS);
        message.setSubject(mail.getTitle());
        message.setText(mail.getMessage());

        mailSender.send(message);
    }
}
