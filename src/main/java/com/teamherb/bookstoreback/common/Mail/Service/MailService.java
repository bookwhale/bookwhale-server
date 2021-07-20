package com.teamherb.bookstoreback.common.Mail.Service;

import com.teamherb.bookstoreback.common.Mail.MailHandler;
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

        /*SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getAddress());
        message.setFrom(MailService.FROM_ADDRESS);
        message.setSubject(mail.getTitle());
        message.setText(mail.getMessage());

        mailSender.send(message);*/
        try {
            MailHandler mailHandler = new MailHandler(mailSender);

            // 받는 사람
            mailHandler.setTo(mail.getAddress());
            // 보내는 사람
            mailHandler.setFrom(MailService.FROM_ADDRESS);
            // 제목
            mailHandler.setSubject(mail.getTitle());
            // HTML Layout
            String htmlContent = "<p>" + mail.getMessage() +"<p> <img src='cid:teamherb'>";
            mailHandler.setText(htmlContent, true);
            // 첨부 파일
            //mailHandler.setAttach("newTest.txt", "static/originTest.txt");
            // 이미지 삽입
            mailHandler.setInline("teamherb", "static/teamherb.png");

            mailHandler.send();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
