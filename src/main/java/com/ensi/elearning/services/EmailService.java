package com.ensi.elearning.services;

import com.ensi.elearning.repositories.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {


    private  static final Logger logger =  LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @SneakyThrows
    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            try {
                helper.setText(email, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("gabsiaziz37@gmail.com");
            mailSender.send(mimeMessage);
        }catch (MessagingException m){
            logger.error("failed to send email", m);
            throw new IllegalStateException("failed to send email");
        }
    }
}
