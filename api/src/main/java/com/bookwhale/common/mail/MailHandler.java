package com.bookwhale.common.mail;

import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailHandler {

  private final JavaMailSender sender;

  private final MimeMessage message;

  private final MimeMessageHelper messageHelper;

  public MailHandler(JavaMailSender jSender) throws Exception {
    this.sender = jSender;
    message = jSender.createMimeMessage();
    messageHelper = new MimeMessageHelper(message, true, "UTF-8");
  }

  public void setFrom(String fromAddress) throws Exception {
    messageHelper.setFrom(fromAddress);
  }

  public void setTo(String email) throws Exception {
    messageHelper.setTo(email);
  }

  public void setSubject(String subject) throws Exception {
    messageHelper.setSubject(subject);
  }

  public void setText(String text, boolean useHtml) throws Exception {
    messageHelper.setText(text, useHtml);
  }

  public void send() {
    sender.send(message);
  }
}
