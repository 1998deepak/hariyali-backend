package com.hariyali.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.entity.Document;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.DocumentRepository;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private DonationRepository donationRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Value("${file.path}")
	String FILE_PATH ;
	
//	String FILE_PATH1="C:\\Users\\DELL\\Desktop\\New folder";

	// method to generate new donor or donation id
	public String createDonarIDORDonationID(String idForEntity) {
		System.err.println(idForEntity);
		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		int month = currentDate.getMonthValue();
		String formattedResult = null;
		int lastFiveDigits;
		String lastFiveDigitsStr = null;

		if (idForEntity.equalsIgnoreCase("user")) {
			String lastDonarID = usersRepository.getLastDonorID();
			if (lastDonarID == null)
				return "DID" + year + month + "00001";
			lastFiveDigitsStr = lastDonarID.substring(lastDonarID.length() - 5);
			lastFiveDigits = Integer.parseInt(lastFiveDigitsStr) + 1;
			formattedResult = String.format("%05d", lastFiveDigits);
			return "DID" + year + String.format("%02d", month) + formattedResult;
		} else if (idForEntity.equalsIgnoreCase("donation")) {
			String lastDonationID = donationRepository.getLastDonationID();
			if (lastDonationID == null)
				return "DN" + year + month + "00001";
			lastFiveDigitsStr = lastDonationID.substring(lastDonationID.length() - 5);
			lastFiveDigits = Integer.parseInt(lastFiveDigitsStr) + 1;
			formattedResult = String.format("%05d", lastFiveDigits);
			return "DN" + year + String.format("%02d", month) + formattedResult;
		} else if (idForEntity.equalsIgnoreCase("DOCUMENT")) {
			String lastDocId = documentRepository.getLastDocID();
			if (lastDocId == null)
				return "DOC" + year + month + "00001";
			lastFiveDigitsStr = lastDocId.substring(lastDocId.length() - 5);
			lastFiveDigits = Integer.parseInt(lastFiveDigitsStr) + 1;
			formattedResult = String.format("%05d", lastFiveDigits);
			return "DOC" + year + String.format("%02d", month) + formattedResult;
		} else {
			log.error("send appropriate idForEntity value..");
			throw new CustomException("send appropriate idForEntity value..");
		}

	}

	public String getDonarFileFilePath(String emailID) {
		String folderName = FILE_PATH +File.separator + emailID;
		// Create the folder
		Path folder = Paths.get(folderName);
		log.info("folder Path=>" + folderName);
		if (!Files.exists(folder)) {
			try {
				Files.createDirectory(folder);
				log.info(folderName+"=Folder created successfully.");

			} catch (IOException e) {
				System.err.println("Failed to create folder: " + e.getMessage());
			}
		} else {
			log.info("Folder already exists.");
		}
		return folderName;

	}

	public void saveDocumentDetails(String idForEntity, String fileName, String filePath, String fileType,
			String docType, Donation donation) {
		System.err.println("idForEntity"+idForEntity);
		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		Document document = new Document();
		document.setDocId(createDonarIDORDonationID(idForEntity));
		System.err.println(createDonarIDORDonationID(idForEntity));
		document.setFileName(fileName);
		document.setFilePath(filePath);
		document.setFileType(fileType);
		document.setDocType(docType);
		document.setCreatedDate(new Date());
		document.setUpdatedDate(new Date());
		document.setDonation(donation);
		if (donation != null) {
			document.setCreatedBy(donation.getCreatedBy() == null ? "" : donation.getCreatedBy());
			document.setModifiedBy(donation.getModifiedBy() == null ? "" : donation.getModifiedBy());
		}
		document.setYear(year);
//		Document document2 = documentRepository.findByYearAndDocTypeAndUsers(year, docType,users);
//		if (document2 != null) {
//			document.setId(document2.getId());
//			documentRepository.save(document);
//		}
		documentRepository.save(document);

	}
	public void saveDocumentDetails(String idForEntity, String fileName, String filePath, String fileType,
			String docType,Users users) {
		System.err.println("idForEntity"+idForEntity);
		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		Document document = new Document();
		document.setDocId(createDonarIDORDonationID(idForEntity));
		System.err.println(createDonarIDORDonationID(idForEntity));
		document.setFileName(fileName);
		document.setFilePath(filePath);
		document.setFileType(fileType);
		document.setDocType(docType);
		document.setCreatedDate(new Date());
		document.setUpdatedDate(new Date());
		document.setUsers(users);
		if (users != null) {
			document.setCreatedBy(users.getCreatedBy() == null ? "" : users.getCreatedBy());
			document.setModifiedBy(users.getModifiedBy() == null ? "" : users.getModifiedBy());
		}
		document.setYear(year);
//		Document document2 = documentRepository.findByYearAndDocTypeAndUsers(year, docType,users);
//		if (document2 != null) {
//			document.setId(document2.getId());
//			documentRepository.save(document);
//		}
		documentRepository.save(document);

	}


	public String getFilePath(String fileName) {
		ClassLoader classLoader = CommonService.class.getClassLoader();
		URL resourceUrl = classLoader.getResource(fileName); // Specify the resource path

		if (resourceUrl != null) {
			// Convert the resource URL to a file
			File file = new File(resourceUrl.getFile());

			if (file.exists()) {
				// Now you can work with the file
				log.info("File exists: " + file.getAbsolutePath());
				return file.getAbsolutePath();
			} else {
				log.info("File does not exist.");
			}
		} else {
			log.info("Resource not found.");
		}
		return null;
	}

	public String generatePassword() {
		SecureRandom random = new SecureRandom();
		Pattern pattern = Pattern.compile(EnumConstants.PASSWORD_PATTERN);

		String password;
		Matcher matcher;

		do {
			StringBuilder passwordBuilder = new StringBuilder(20);

			for (int i = 0; i < 8; i++) {
				int randomIndex = random.nextInt(EnumConstants.CHARACTERS.length());
				char randomChar = EnumConstants.CHARACTERS.charAt(randomIndex);
				passwordBuilder.append(randomChar);
			}

			password = passwordBuilder.toString();
			matcher = pattern.matcher(password);

		} while (!matcher.matches());

		return password;
	}
}
