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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	@Autowired
	CCServiceEmailAPI ccServiceEmailAPI;

	public void sendSimpleEmail(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, body);
		log.info("Mail Sent...");

	}

	public void sendSimpleEmailToHariyaliTeam(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendSupportMail(toEmail, subject, body);
		log.info("Mail Sent To Hariyali Team ...");

	}

	public void sendWelcomeLetterMail(String to, String subject, String text, Users user) {
		String body = String.format(text, user.getFirstName() + " " + user.getLastName(), user.getEmailId(),
				user.getPassword());
		ccServiceEmailAPI.sendCorrespondenceMail(to, subject, body);
		log.info("Mail send");
	}

	public void sendGiftingLetterEmail(Users recipientData, String donationEvent) {
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;
		String mailBody = String.format(body, donationEvent, recipientData.getEmailId(), recipientData.getPassword());
		ccServiceEmailAPI.sendCorrespondenceMail(recipientData.getEmailId(), subject, mailBody);
		log.info("Mail Sent...");
	}

	public void sendWebIdEmail(String toEmail, Users user) {
		String body = "Dear Sponsor,<br> <p>Welcome to Project Hariyali.</p>"
				+ "The Mahindra Foundation and Naandi Foundation would like to thank you for your donation."
				+ "Below is your Web Id :<b>" + user.getWebId() + "</b><br>Wait For Admin Approval.<br>"
				+ "Thanks & Regards,<br>" + "Mahindra Foundation<br>" + "Sheetal Mehta<br>"
				+ "Trustee & Executive Director<br>" + "K.C. Mahindra Education Trust,<br>"
				+ "3rd Floor, Cecil Court,<br>" + "Near Regal Cinema,<br>" + "Mahakavi Bushan Marg,<br>"
				+ "Mumbai 400001<br>" + "PS : Contact support@hariyali.org.in in case of any query.";
		try {
			ccServiceEmailAPI.sendCorrespondenceMail(toEmail, "Web Id for Plant Donation", body);
		} catch (EmailNotConfiguredException e) {
			throw new EmailNotConfiguredException(e.getMessage());
		}
		log.info("Mail send successfully");

	}

	public void sendPlantationEmail(String toEmail, String subject, String bodyMessage) {
		ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, bodyMessage);
	}

	public void sendReceiptWithAttachment(Users user,String orderId, Receipt receipt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String formattedDate = sdf.format(receipt.getRecieptDate());
		String name = user.getFirstName() + " " + user.getLastName();
		FileSystemResource resource = new FileSystemResource(receipt.getReciept_Path());
		File[] files = { resource.getFile() };
		String text = "Dear Tree Planter \"%s\" Green Warrior<br><br>"
				+ "<p>We thank you for your donation dated %s ,ref no %s<br></p>"
				+ "Please find a PDF version of the receipt attached herewith."
				+ "<p>Thanking you for your support to project Hariyali.</p><br>Team Hariyali<br>Naandi Foundation<br>"+"<br>PS : Contact 'support@hariyali.org.in' in case of any query.<br>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(text, name, formattedDate, orderId);
		ccServiceEmailAPI.sendPaymentsMail(user.getEmailId(), "Receipt For Your Donation", mailBody, files);
	}

	public void sendThankyouLatter(String to, Users user) {
		String subject = EnumConstants.thankYouLetterSuject;
		String body = EnumConstants.thankYouLetterContent;
		FileSystemResource resource = new FileSystemResource("src/main/resources/thankyouletter.jpg");
		File[] files = { resource.getFile() };
		String mailBody = String.format(body, user.getFirstName() + " " + user.getLastName());
		ccServiceEmailAPI.sendCorrespondenceMailwithAttachment(user.getEmailId(), subject, mailBody,files);
		log.info("Mail Sent...");
	}
}
