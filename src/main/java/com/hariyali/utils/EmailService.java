package com.hariyali.utils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.entity.Users;
import com.hariyali.repository.ReceiptRepository;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Autowired
	ReceiptRepository receiptRepository;

	public void sendSimpleEmail(String toEmail, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		mailSender.send(message);
		System.out.println("Mail Sent...");

	}

	public void sendSimpleEmailToHariyaliTeam(String toEmail, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(toEmail);
		message.setTo(fromEmail);
		message.setText(body);
		message.setSubject(subject);
		mailSender.send(message);
		System.out.println("Mail Sent To Hariyali Team ...");

	}

	public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath,Users user)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(String.format(text, user.getDonorId()));
		FileSystemResource file = new FileSystemResource(attachmentPath);
		helper.addAttachment(file.getFilename(), file, "application/pdf");
		mailSender.send(mimeMessage);
	}

	public void sendGiftingLetterEmail(String toEmail, Users resulEntity) {
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;
		String mailBody = String.format(body, toEmail, resulEntity.getFirstName());
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setText(mailBody);
		message.setSubject(subject);
		mailSender.send(message);
		System.out.println("Mail Sent...");
	}
	
}
