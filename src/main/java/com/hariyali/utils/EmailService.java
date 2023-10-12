package com.hariyali.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.entity.ContactUs;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Plantation;
import com.hariyali.entity.PlantationMaster;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Recipient;
import com.hariyali.entity.UserPackages;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.EmailNotConfiguredException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.serviceimpl.CCServiceEmailAPI;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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

	@Value("${jasper.filepath}")
	String jasperFilePath;

	@Value("${jasper.imagespath}")
	String jasperImagesPath;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommonService commonService;

	public void sendSimpleEmail(String toEmail, String subject, String body) {
		ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, body);
		log.info("Mail Sent...");

	}

	public void sendSimpleEmailToHariyaliTeam(ContactUs contactUs) {
		String body = "Dear team,\n Donar "+contactUs.getContactName()+" contact us with "+contactUs.getMassage() + " " + contactUs.getContactEmail() + " mail Id of "+ contactUs.getContactName();
		String subject = contactUs.getContactSubject();
		ccServiceEmailAPI.sendSupportMail(subject ,body );
		log.info("Mail Sent To Hariyali Team ...");

	}

	public void sendWelcomeLetterMail(String to, String subject, String text, Users user) {
		String password = commonService.generatePassword();
		user.setPassword(passwordEncoder.encode(password));
		log.info(password);
		userRepository.save(user);
		log.info("Password set");
		String body = String.format(text, user.getFirstName(), user.getEmailId(), password);
		ccServiceEmailAPI.sendCorrespondenceMail(to, subject, body);
		log.info("Mail send");
	}

	public void sendGiftingLetterEmail(Donation donation, Users recipientData, String donationEvent, String path) {
		int noOfPlant = donationRepository.getNoOfPlants(donation.getDonationId());
		String giftorMail = userRepository.getGiftorEmailByDonation(donation.getDonationId());
		String giftorFirstName = userRepository.getGiftorFirstNameByDonation(donation.getDonationId());
		String giftorLastName=userRepository.getGiftorLastNameByDonation(donation.getDonationId());
		FileSystemResource resource = new FileSystemResource(path);
		File[] files = { resource.getFile() };
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;
		String mailBody = String.format(body, donation.getRecipient().get(0).getFirstName(), noOfPlant, giftorFirstName+" "+giftorLastName);
		ccServiceEmailAPI.sendCorrespondenceMailForGift(donation.getRecipient().get(0).getEmailId(), subject, mailBody, giftorMail,
				files);
		log.info("Mail Sent...");
	}
	
	public void sendGiftingLetterEmailCorporate(Donation donation, Users recipientData, String donationEvent,String organization, String path) {
		int noOfPlant = donationRepository.getNoOfPlants(donation.getDonationId());
		String giftorMail = userRepository.getGiftorEmailByDonation(donation.getDonationId());
		FileSystemResource resource = new FileSystemResource(path);
		File[] files = { resource.getFile() };
		String subject = EnumConstants.GIFTING_MSG_SUBJECT;
		String body = EnumConstants.GIFTING_MSG_BODY;
		String mailBody = String.format(body, donation.getRecipient().get(0).getFirstName(), noOfPlant,organization);
		ccServiceEmailAPI.sendCorrespondenceMailForGift(donation.getRecipient().get(0).getEmailId(), subject, mailBody, giftorMail,
				files);
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
				+ "Team Hariyali<br>"
				+ "3rd Floor, Cecil Court,"
				+ "Near Regal Cinema,<br>" + "Mahakavi Bhushan Marg," + "Mumbai 400001<br>"
				+ "PS : Contact 'support@hariyali.org.in' in case of any query.<br>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		String mailBody = String.format(text, name, formattedDate, orderId);
		ccServiceEmailAPI.sendPaymentsMail(user.getEmailId(), "Project Hariyali – Receipt towards your donation",
				mailBody, files);
	}

	public void sendThankyouLatter(String to, Users user) {
		String subject = EnumConstants.thankYouLetterSuject;
		String body = EnumConstants.thankYouLetterContent;
		FileSystemResource resource = null;
			Map<String, String> responseCertifiate = generateCertificateForThankYou(user.getFirstName(), user.getEmailId());
			commonService.saveDocumentDetails("DOCUMENT", responseCertifiate.get("filePath"),
					responseCertifiate.get("outputFile"), "PDF", "CERTIFICATE", user, user);
			resource = new FileSystemResource(responseCertifiate.get("outputFile"));
			File[] files = { resource.getFile() };
			String mailBody = String.format(body, user.getFirstName());
			ccServiceEmailAPI.sendCorrespondenceMailwithAttachment(user.getEmailId(), subject, mailBody, files);
			log.info("Mail Sent...");
	}

	public Path getFileFromPath(String filename) throws IOException {

		Path path = Paths.get(FILE_PATH + "/IMAGES/" + filename);
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

	public void sendPlantationMail(UserPackages packages, PlantationMaster plantationMaster,Integer allocatedPlant) {
		String date = toDateString(plantationMaster.getPlantationDate(), "dd-MM-yyyy");
		String subject = "Project Hariyali – Plantation Report";
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		String year = yearFormat.format(plantationMaster.getPlantationDate());
		String messsage = "Dear " + packages.getUserDonation().getUsers().getFirstName()
				+ ",<br>Thank you for contributing to Project Hariyali.<br>" + "For the donation dated, " + date + ","
				+ allocatedPlant + " plant/s have been planted in "
				+ plantationMaster.getSeason() + " " + year + " in the village " + plantationMaster.getVillage()
				+ " in the state of " + plantationMaster.getState() + ".<br>"
				+ "We will be taking care of your plants for two years to ensure  its optimum growth and nurturing in the initial years for 100% survival of the sapling.  <br>"
				+ "Thanks once again.<br>" + "<br>" + "Team Hariyali <br>" + "Mahindra Foundation<br>"
				+ "3rd Floor, Cecil Court, Near Regal Cinema,<br>" + "Mahakavi Bhushan Marg, Colaba, <br>"
				+ "Mumbai, Maharashtra -400001<br>"
				+ "<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		log.info("mailBody:" + messsage);
		ccServiceEmailAPI.sendCorrespondenceMail(packages.getUserDonation().getUsers().getEmailId(), subject, messsage);
		log.info("Mail Sent!!!");
	}

	public void sendFirstAnnualPlantationMail(Plantation plantationMaster) {
		String date=toDateString(plantationMaster.getPlantationMaster().getPlantationDate(), "dd-MM-yyyy");
		String subject = "Project Hariyali – 1st Annual Report";
		String content = "Dear "+plantationMaster.getUserPackages().getUserDonation().getUsers().getFirstName()+",<br>Thank you for contributing to Project Hariyali.	<br>"
				+ "For the donations dated &ndash; "+date+",  we are happy to report that "+ plantationMaster.getNoOfPlantsPlanted()+" plants are healthy and growing well.<br>"
				+ "We thank you once again for your contribution to our planet earth.<br>" + "<br>"
				+ "Team Hariyali <br>" + "Mahindra Foundation<br>" + "3rd Floor, Cecil Court, Near Regal Cinema,<br>"
				+ "Mahakavi Bhushan Marg, Colaba, <br>" + "Mumbai, Maharashtra &ndash; 400001<br>"
				+ "<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		ccServiceEmailAPI.sendCorrespondenceMail(
				plantationMaster.getUserPackages().getUserDonation().getUsers().getEmailId(), subject, content);
	}

	public void sendSecondAnnualPlantationMail(Plantation plantationMaster) {
		String date=toDateString(plantationMaster.getPlantationMaster().getPlantationDate(), "dd-MM-yyyy");
		String subject = "Project Hariyali – 2nd Annual Report";
		String content ="Dear "+plantationMaster.getUserPackages().getUserDonation().getUsers().getFirstName()+ ",<br>Thank you for contributing to Project Hariyali.<br>"
				+ "For the donations dated &ndash; "+date+" ,  we are happy to report that "+ plantationMaster.getNoOfPlantsPlanted()+"  plants are healthy and growing well.<br>"
				+ "We thank you once again for your contribution to our planet earth.<br><br>"
				+ "Team Hariyali <br>" + "Mahindra Foundation<br>" + "3rd Floor, Cecil Court, Near Regal Cinema,<br>"
				+ "Mahakavi Bhushan Marg, Colaba, <br>" + "Mumbai, Maharashtra &ndash; 400001<br>"
				+ "<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
				+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
		ccServiceEmailAPI.sendCorrespondenceMail(
				plantationMaster.getUserPackages().getUserDonation().getUsers().getEmailId(), subject, content);
	}

	private String toDateString(Date date, String dateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(date);
	}
	public Map<String, String> generateCertificateForThankYou(String donarName, String emailID) {

		String filepath = null;
		String reportName = null;
		String imagesPathName = null;
		Map<String, String> response = new HashMap<>();

		try {
			reportName = "SimpleDonation.jrxml";
			imagesPathName = jasperImagesPath + File.separator + "simpleDonation.png";

			Map<String, Object> parameters = new HashMap<String, Object>();
			filepath = jasperFilePath + reportName;
			parameters.put("donarName", donarName);
			parameters.put("ImageParameter", imagesPathName);
//			filepath="\\hariyali-backend\\src\\main\\resources\\META-INF\\jasperReports\\Festival.jrxml";
			JasperReport jasperReport = JasperCompileManager.compileReport(filepath);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

			File outputFile = null;
			String path = commonService.getDonarFileFilePath(emailID);
			if (path != null) {
				String pdfFilePath = path + File.separator + "ThankYou" + ".pdf";
				log.info("Pdf file path=>" + pdfFilePath);
				outputFile = new File(pdfFilePath);
				JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath); // Export to PDF
				System.err.println(outputFile.getName());
				response.put("filePath", outputFile.getName());
				response.put("outputFile", outputFile.toString());

			}

		} catch (Exception e) {
			log.info(e.getMessage());
			throw new CustomException(e.getMessage());

		}

		return response;
	}

}
