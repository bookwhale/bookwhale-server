package com.teamherb.bookstoreback.common.Mail.service;

import com.teamherb.bookstoreback.common.Mail.MailHandler;
import com.teamherb.bookstoreback.common.Mail.dto.Maildto;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "hose123@ajou.ac.kr";

    public void mailSend(Maildto maildto) {

        /*SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getAddress());
        message.setFrom(MailService.FROM_ADDRESS);
        message.setSubject(mail.getTitle());
        message.setText(mail.getMessage());

        mailSender.send(message);*/
        try {
            MailHandler mailHandler = new MailHandler(mailSender);

            // 받는 사람
            mailHandler.setTo(maildto.getAddress());
            // 보내는 사람
            mailHandler.setFrom(MailService.FROM_ADDRESS);
            // 제목
            mailHandler.setSubject(maildto.getTitle());
            // HTML Layout
            String htmlContent = "<p>" + maildto.getMessage() +"<p> <img src='cid:teamherb'>";
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
