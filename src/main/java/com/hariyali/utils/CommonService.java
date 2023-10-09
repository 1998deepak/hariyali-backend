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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.UsersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import static java.util.Optional.ofNullable;

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
			String docType,Users users, Users userCreatedBy) {
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
			document.setCreatedBy(userCreatedBy.getCreatedBy() == null ? "" : userCreatedBy.getCreatedBy());
			document.setModifiedBy(userCreatedBy.getModifiedBy() == null ? "" : userCreatedBy.getModifiedBy());
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

	public ApiResponse<List<Document>> getUserDocuments(PaginationRequestDTO<Integer> dto){
		ApiResponse<List<Document>> response = new ApiResponse<>();

		Page<Object[]> documents = documentRepository.findByUserId(dto.getData(), PageRequest.of(dto.getPageNumber(), dto.getPageSize()));
		if(!documents.isEmpty()){

			response.setData(documents.getContent().stream().map(this::toDocument).collect(Collectors.toList()));
			response.setTotalRecords(documents.getTotalElements());
			response.setStatus("Success");
		} else{
			throw new CustomException("No document found");
		}
		return response;
	}
	
	private Document toDocument(Object[] document){
		Document dto = new Document();
		if (document.length > 0) {
			dto.setId(ofNullable(document[0]).map(String::valueOf).map(Integer::parseInt).orElse(0));
			dto.setDocId(ofNullable(document[1]).map(String::valueOf).orElse(""));
			dto.setDocType(ofNullable(document[2]).map(String::valueOf).orElse(""));
			dto.setFileName(ofNullable(document[3]).map(String::valueOf).orElse(""));
			dto.setFilePath(ofNullable(document[4]).map(String::valueOf).orElse(""));
			dto.setFileType(ofNullable(document[5]).map(String::valueOf).orElse(""));
			dto.setYear(ofNullable(document[6]).map(String::valueOf).map(Integer::valueOf).orElse(0));
		}
		return dto;
	}
	
}
