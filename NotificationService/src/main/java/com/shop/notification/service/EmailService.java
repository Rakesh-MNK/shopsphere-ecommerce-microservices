package com.shop.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Value("${app.mail.from}")
    private String fromEmail;

    public void sendSimpleEmail(String recipientEmail, String type, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // sender's email
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        log.info("Email sent successfully to: {}", recipientEmail);
        
    }

}
