package com.example.hospital.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    @Async
    public void emailSender(String to, String subject, String html) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
