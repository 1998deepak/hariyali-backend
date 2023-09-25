package com.hariyali.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.dto.PlantationMasterDTO;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.EmailNotConfiguredException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.serviceimpl.CCServiceEmailAPI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
	
	@Autowired
	UsersRepository userRepository;

	@Autowired
	DonationRepository donationRepository;

	@Autowired
	CCServiceEmailAPI ccServiceEmailAPI;
	
	@Value("${file.path}")
	String FILE_PATH;

	@Autowired
	private PasswordEncoder passwordEncoder;

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
		String password = commonService.generatePassword();
		user.setPassword(passwordEncoder.encode(password));
		log.info(password);
		userRepository.save(user);
		String body = String.format(text, user.getFirstName(), user.getEmailId(), password);
		ccServiceEmailAPI.sendCorrespondenceMail(to, subject, body);
		log.info("Mail send");
	}

	public void sendGiftingLetterEmail(Users recipientData, String donationEvent,String path) {
		FileSystemResource resource = new FileSystemResource(path);
		File[] files = { resource.getFile() };
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;

		String mailBody = String.format(body, recipientData.getEmailId());
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
		ccServiceEmailAPI.sendPaymentsMail(user.getEmailId(), "Project Hariyali –Receipt towards your donation",
				mailBody, files);
	}
  
public void sendThankyouLatter(String to, Users user) {
		String subject = EnumConstants.thankYouLetterSuject;
		String body = EnumConstants.thankYouLetterContent;

		FileSystemResource resource = null;
		try {
			resource = new FileSystemResource(getFileFromPath("thankyouletter.jpg").toString());
			System.out.println("Thanks=>"+getFileFromPath("thankyouletter.jpg").toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File[] files = { resource.getFile() };
		String mailBody = String.format(body, user.getFirstName());
		ccServiceEmailAPI.sendCorrespondenceMailwithAttachment(user.getEmailId(), subject, mailBody, files);
		log.info("Mail Sent...");
	}

	public Path getFileFromPath(String filename) throws IOException {
		
		Path path = Paths.get(FILE_PATH+"/IMAGES/" + filename);
		System.out.println("Path:" + path);
		return path;
	}

	public void sendDonationRejectionMail(Users user) {
		Donation donation = donationRepository.getDonationByWebId(user.getWebId());
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		String strDate = formatter.format(donation.getCreatedDate());
		String subject = "Project Hariyali – Donation Failure";
		String content = "Dear %s,<br><br>" + "<p>Thank you for your interest in Project Hariyali."
				+ "Unfortunately we cannot proceed with the donation dated %s reference ID %s.</p><br><br>"
				+ "Thank you.<br>" + "Team Hariyali<br>" + "Mahindra Foundation<br>"
				+ "3rd Floor, Cecil Court,Near Regal Cinema,<br>" + "Mahakavi Bushan Marg,Colaba.<br>"
				+ "Mumbai, Maharashtra  - 400001<br>"
				+ "<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(content, user.getFirstName(), strDate, donation.getOrderId());
		ccServiceEmailAPI.sendCorrespondenceMail(user.getEmailId(), subject, mailBody);

	}
	
	public void sendPlantationMail(Users user,PlantationMasterDTO plantationMasterDTO) {
		String subject="Project Hariyali – Plantation Report";
		String content="Dear %s<br>"
				+ "Thank you for contributing to Project Hariyali.<br>"
				+ "For the donation dated,%s,%d plant/s have been planted in %s %s in the village %s, in the state of %s.<br>"
				+ "&nbsp;&nbsp; We will be taking care of your plants for two years to ensure  its optimum growth and nurturing in the initial years for 100% survival of the sapling.  <br>"
				+ "Thanks once again.<br>"
				+ "<br>"
				+ "Team Hariyali <br>"
				+ "Mahindra Foundation<br>"
				+ "3rd Floor, Cecil Court, Near Regal Cinema,<br>"
				+ "Mahakavi Bushan Marg, Colaba, <br>"
				+ "Mumbai, Maharashtra – 400001<br>"
				+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String year = yearFormat.format(plantationMasterDTO.getPlantationDate());
		String mailBody = String.format(content,user.getFirstName(),plantationMasterDTO.getPlantationDateString(), plantationMasterDTO.getNoOfPlantsPlanted(), plantationMasterDTO.getSeason(),year,plantationMasterDTO.getVillage(),plantationMasterDTO.getState());
		ccServiceEmailAPI.sendCorrespondenceMail(user.getEmailId(), subject, mailBody);
	}
	
	public void sendFirstAnnualPlantationMail(Users user,PlantationMasterDTO plantationMasterDTO) {
		String subject="Project Hariyali – 1st Annual Report";
		String content="Dear %s<br>"
				+ "Thank you for contributing to Project Hariyali.	<br>"
				+ "For the donations dated – __<DD-MON-YYYY>__,  we are happy to report that <number of 	plants> plants are healthy and growing well.<br>"
				+ "&nbsp;&nbsp;We thank you once again for your contribution to our planet earth.<br>"
				+ "<br>"
				+ "Team Hariyali <br>"
				+ "Mahindra Foundation<br>"
				+ "3rd Floor, Cecil Court, Near Regal Cinema,<br>"
				+ "Mahakavi Bushan Marg, Colaba, <br>"
				+ "Mumbai, Maharashtra – 400001<br>"
				+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(content,user.getFirstName());
		ccServiceEmailAPI.sendCorrespondenceMail(user.getEmailId(), subject, mailBody);
	}
	
	public void sendSecondAnnualPlantationMail(Users user,PlantationMasterDTO plantationMasterDTO) {
		String subject="Project Hariyali – 2nd Annual Report";
		String content="Dear %s<br>"
				+ "Thank you for contributing to Project Hariyali.	<br>"
				+ "For the donations dated – __<DD-MON-YYYY>__,  we are happy to report that <number of 	plants> plants are healthy and growing well.<br>"
				+ "&nbsp;&nbsp;We thank you once again for your contribution to our planet earth.<br>"
				+ "<br>"
				+ "Team Hariyali <br>"
				+ "Mahindra Foundation<br>"
				+ "3rd Floor, Cecil Court, Near Regal Cinema,<br>"
				+ "Mahakavi Bushan Marg, Colaba, <br>"
				+ "Mumbai, Maharashtra – 400001<br>"
				+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(content,user.getFirstName());
		ccServiceEmailAPI.sendCorrespondenceMail(user.getEmailId(), subject, mailBody);
	}
}
