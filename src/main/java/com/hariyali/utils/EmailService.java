package com.hariyali.utils;

import java.io.File;
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
import com.hariyali.exceptions.EmailNotConfiguredException;
import com.hariyali.serviceimpl.CCServiceEmailAPI;

@Service
public class EmailService {

	@Autowired
	CCServiceEmailAPI ccServiceEmailAPI;

	public void sendSimpleEmail(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, body);
		System.out.println("Mail Sent...");

	}

	public void sendSimpleEmailToHariyaliTeam(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendSupportMail(toEmail, subject, body);
		System.out.println("Mail Sent To Hariyali Team ...");

	}

//	public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath, Users user)
//			throws MessagingException {
//		MimeMessage mimeMessage = mailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//		helper.setTo(to);
//		helper.setSubject(subject);
//		helper.setText(String.format(text, user.getEmailId(),user.getPassword()));
//		FileSystemResource file = new FileSystemResource(attachmentPath);
//		helper.addAttachment(file.getFilename(), file, "application/pdf");
//		mailSender.send(mimeMessage);
//		System.out.println("Mail send");
//	}

	public void sendWelcomeLetterMail(String to, String subject, String text, Users user) {
		String body = String.format(text, user.getEmailId(), user.getPassword());
		ccServiceEmailAPI.sendCorrespondenceMail(to, subject, body);
		System.out.println("Mail send");
	}

	public void sendGiftingLetterEmail(Users recipientData,String donationEvent) {
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;
		String mailBody = String.format(body, donationEvent, recipientData.getEmailId(), recipientData.getPassword());
		ccServiceEmailAPI.sendCorrespondenceMail(recipientData.getEmailId(), subject, mailBody);
		System.out.println("Mail Sent...");
	}

	public void sendWebIdEmail(String toEmail, Users user) {
		String body = "Dear Sponsor,<br> <p>Welcome to Project Hariyali.</p>"
				+ "The Mahindra Foundation and Naandi Foundation would like to thank you for your donation."
				+ "Below is your Web Id :<b>" + user.getWebId() + "</b><br>Wait For Admin Approval.<br>"
				+ "<br>Best wishes,<br>Team Hariyali<br>" + "<br>";
		try {
			ccServiceEmailAPI.sendCorrespondenceMail(toEmail, "Web Id for Plant Donation", body);
		} catch (EmailNotConfiguredException e) {
			throw new EmailNotConfiguredException(e.getMessage());
		}
		System.err.println("Mail send successfully");

	}

	public void sendPlantationEmail(String toEmail, String subject, String bodyMessage) {
		ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, bodyMessage);
	}

	public void sendReceiptWithAttachment(String to, Receipt receipt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String formattedDate = sdf.format(receipt.getRecieptDate());
		FileSystemResource resource = new FileSystemResource(receipt.getReciept_Path());
		File[] files = {resource.getFile()};
		String text = "Dear Sponsor,<br>" + "<p>We thank you for your sponsorship.<br>" + "Rec.No:"
				+ receipt.getRecieptNumber() + " Date:" + formattedDate + "<br></p>"
				+ "Please find a PDF version of the receipt attached herewith.<br>"
				+ "<p>Thanking you for your support to project Hariyali.</p><br>Naandi Foundation<br>Address : <br>PS : Contact 'support@hariyali.org.in' in case of any query.";
		ccServiceEmailAPI.sendPaymentsMail(to, "Receipt For Your Donation", text,files);
	}
}
