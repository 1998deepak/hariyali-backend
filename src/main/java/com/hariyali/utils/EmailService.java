package com.hariyali.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.hariyali.entity.Receipt;
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

	public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath, Users user)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(String.format(text, user.getEmailId(),user.getPassword()));
		FileSystemResource file = new FileSystemResource(attachmentPath);
		helper.addAttachment(file.getFilename(), file, "application/pdf");
		mailSender.send(mimeMessage);
		System.out.println("Mail send");
	}
	
	public void sendWelcomeLetterMail(String to, String subject, String text,  Users user)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(String.format(text, user.getEmailId(),user.getPassword()));
//		FileSystemResource file = new FileSystemResource(attachmentPath);
//		helper.addAttachment(file.getFilename(), file, "application/pdf");
		mailSender.send(mimeMessage);
		System.out.println("Mail send");
	}

	public void sendGiftingLetterEmail(Users recipientData,String donationEvent) {
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;
		String mailBody = String.format(body,donationEvent, recipientData.getEmailId(),recipientData.getPassword());
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(recipientData.getEmailId());
		message.setText(mailBody);
		message.setSubject(subject);
		mailSender.send(message);
		System.out.println("Mail Sent...");
	}

	public void sendWebIdEmail(String toEmail, Users user) {
		String body = "Dear Sir/Madam,\n \tWelcome to Project Hariyali."
				+ "The Mahindra Foundation,would like to thank you for your donation to Project Hariyali. The main objective of the project is to do 5 Billion Tree Plantation from 2026 in several parts of the Nation. "
				+ "The Tree Plantation is the main Agenda of the Project. "
				+ "The HARIYALI is a Partnership between Mahindra and Mahindra and the Nandi Foundation. The Project will be jointly managed by M&M and Nandi Foundation. \r\n"
				+ "Below is your Web Id :" + user.getWebId() + "\rWait For Admin Approval.\\r\\n"
				+ "\nBest wishes,\nTeam Hariyali\r\n" + "\r\n";
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Web Id for Plant Donation");
		message.setText(body);
		mailSender.send(message);

		System.err.println("Mail send successfully");

	}

	public void sendPlantationEmail(String toEmail, String subject, String bodyMessage) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(bodyMessage, true); // Set to true for HTML content if needed

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			// Handle any exceptions here
			e.printStackTrace();
		}
	}

	public void sendReceiptWithAttachment(String to, Receipt receipt) throws MessagingException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = sdf.format(receipt.getRecieptDate());
		String text = "Dear Sponsor,\n" +
	            "\t\tWe thank you for your sponsorship.\n" +
	            "Rec.No:"+ receipt.getRecieptNumber() +" Date:"+ formattedDate+"\n"+
	            "Please find a PDF version of the receipt attached herewith.\n" +
	            "Thanking you for your support to project Hariyali.\n" +
	            "\n" +
	            "Naandi Foundation\n" +
	            "\tAddress : \n" +
	            "PS : Contact 'support@hariyali.org.in' in case of any query.";
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(to);
		helper.setSubject("Receipt For Your Donation");
		helper.setText(text);
		FileSystemResource file = new FileSystemResource(receipt.getReciept_Path());
		helper.addAttachment(file.getFilename(), file, "application/pdf");
		mailSender.send(mimeMessage);
	}
}
