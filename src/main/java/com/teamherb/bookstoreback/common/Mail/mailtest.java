package com.teamherb.bookstoreback.common.Mail;

import com.teamherb.bookstoreback.common.Mail.Service.MailService;
import com.teamherb.bookstoreback.common.Mail.domain.Mail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class mailtest {

    private final MailService mailService;

    public mailtest(MailService mailService) {
        this.mailService = mailService;
    }
    @ResponseBody
    @PostMapping("/mail")
    public String execMail(@RequestBody Mail mail) {
        mailService.mailSend(mail);
        return "ok";
    }

}
