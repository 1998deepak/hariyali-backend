package com.hariyali.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.EmailNotConfiguredException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.serviceimpl.CCServiceEmailAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	@Autowired
	DonationRepository donationRepository;

	@Autowired
	CCServiceEmailAPI ccServiceEmailAPI;
	
	@Autowired
	private CommonService commonService;

	public void sendSimpleEmail(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, body);
		log.info("Mail Sent...");

	}

	public void sendSimpleEmailToHariyaliTeam(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendSupportMail(toEmail, subject, body);
		log.info("Mail Sent To Hariyali Team ...");

	}

	public void sendWelcomeLetterMail(String to, String subject, String text, Users user) {
		String body = String.format(text, user.getFirstName(), user.getEmailId(),
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

	public void sendReceiptWithAttachment(Users user, String orderId, Receipt receipt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String formattedDate = sdf.format(receipt.getRecieptDate());
		String name = user.getFirstName();
		FileSystemResource resource = new FileSystemResource(receipt.getReciept_Path());
		File[] files = { resource.getFile() };
		String text = "Dear %s,<br>" + "We thank you for your donation dated %s ,ref no %s<br>"
				+ "Please find a PDF version of the receipt attached herewith.<br>"
				+ "Thanking you for your support to Project Hariyali.<br><br>"
				+ "Team Hariyali<br>Naandi Foundation<br>" + "502, Trendset Towers,<br>"
				+ "Road No 2, Banjara Hills,<br>" + "Hyderabad, Telangana - 500 034<br>"
				+ "<br>PS : Contact 'support@hariyali.org.in' in case of any query.<br>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(text, name, formattedDate, orderId);
		ccServiceEmailAPI.sendPaymentsMail(user.getEmailId(),
				"Project Hariyali –Receipt towards your donation", mailBody, files);
	}

	public void sendThankyouLatter(String to, Users user) {
		String subject = EnumConstants.thankYouLetterSuject;
		String body = EnumConstants.thankYouLetterContent;
		Path path1 = Paths.get("/hariyali/src/main/resources/thankyouletter.jpg");
		System.out.println("first 1=>"+path1.toAbsolutePath());
		try {
			System.out.println("first 2=>"+path1.toRealPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Path path2 = Paths.get("thankyouletter.jpg");
		System.out.println("second 1=>"+path2.toAbsolutePath());
		try {
			System.out.println("second 2=>"+path2.toRealPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Path path3 = Paths.get("hariyali/src/main/resources/thankyouletter.jpg");
		System.out.println("tired 1=>"+path3.toAbsolutePath());
		try {
			System.out.println("tired 2=>"+path3.toRealPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileSystemResource resource = new FileSystemResource("///src/main/resources/thankyouletter.jpg");
		File[] files = { resource.getFile() };
		String mailBody = String.format(body, user.getFirstName());
		ccServiceEmailAPI.sendCorrespondenceMailwithAttachment(user.getEmailId(), subject, mailBody, files);
		log.info("Mail Sent...");
	}

	public void sendDonationRejectionMail(Users user) {
		Donation donation = donationRepository.getDonationByWebId(user.getWebId());
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		String strDate = formatter.format(donation.getCreatedDate());
		String subject = "Project Hariyali – Donation Failure";
		String content = "Dear %s,<br><br>" + "<p>Thank you for your interest in Project Hariyali."
				+ "Unfortunately we cannot proceed with the donation dated %s reference ID %s.</p><br><br>"
				+ "Thank you.<br>"
				+ "Team Hariyali<br>" + "Mahindra Foundation<br>"
				+ "3rd Floor, Cecil Court,Near Regal Cinema,<br>" + "Mahakavi Bushan Marg,Colaba.<br>"
				+ "Mumbai, Maharashtra  - 400001<br>"
				+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(content,user.getFirstName(), strDate, donation.getOrderId());
		ccServiceEmailAPI.sendCorrespondenceMail(user.getEmailId(), subject, mailBody);

	}
}
