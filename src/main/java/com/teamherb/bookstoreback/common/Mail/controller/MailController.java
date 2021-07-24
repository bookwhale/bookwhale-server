package com.teamherb.bookstoreback.common.Mail.controller;

import com.teamherb.bookstoreback.common.Mail.service.MailService;
import com.teamherb.bookstoreback.common.Mail.dto.Maildto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/mail")
    public String execMail(@RequestBody Maildto maildto) {
        mailService.mailSend(maildto);
        return "ok";
    }

}
