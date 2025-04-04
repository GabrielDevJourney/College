package com.gabriel.College.service;

import com.gabriel.College.entity.Person;
import com.gabriel.College.entity.VerificationToken;
import com.gabriel.College.repository.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {
	private final JavaMailSender javaMailSender;
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	@Value("${app.base-url}")
	private String baseUrl;

	public EmailService(JavaMailSender javaMailSender, VerificationTokenRepository verificationTokenRepository) {
		this.javaMailSender = javaMailSender;
	}

	@Async
	public void sendEmail(SimpleMailMessage email){
		javaMailSender.send(email);
	}

	public void sendVerificationEmail(String email, VerificationToken token){
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email);
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setText("To confirm your account, please click here : "
				+ baseUrl + "/api/register/confirm-account?token=" + token.getVerificationToken());
		sendEmail(mailMessage);

		log.debug("Confirmation Token: {}", token.getVerificationToken());
	}
}
