package com.hariyali.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hariyali.entity.Document;
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

	String FILE_PATH = "C:\\Users\\admin\\Desktop\\GCP_MOUNT_AREA";

	// method to generate new donor or donation id
	public String createDonarIDORDonationID(String idForEntity) {
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
			return "DN" + year + String.format("%02d", month) + formattedResult;
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
			log.error("send appropriate idForEntity value..!!");
			throw new CustomException("send appropriate idForEntity value..!!");
		}

	}

	public String getDonarFileFilePath(String donorId) {
		String folderName = FILE_PATH + "\\" + donorId;
		// Create the folder
		Path folder = Paths.get(folderName);
		System.out.println("folder Path=>" + folder);
		if (!Files.exists(folder)) {
			try {
				Files.createDirectory(folder);
				System.out.println("Folder created successfully.");

			} catch (IOException e) {
				System.err.println("Failed to create folder: " + e.getMessage());
			}
		} else {
			System.out.println("Folder already exists.");
		}
		return folderName;

	}

	public void saveDocumentDetails(String idForEntity, String fileName, String filePath, String fileType,
			String docType, Users users) {
		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		Document document = new Document();
		document.setDocId(createDonarIDORDonationID(idForEntity));
		document.setFileName(fileName);
		document.setFilePath(filePath);
		document.setFileType(fileType);
		document.setDocType(docType);
		document.setCreatedDate(new Date());
		document.setUpdatedDate(new Date());
		if (users != null) {
			document.setCreatedBy(users.getCreatedBy() == null ? "" : users.getCreatedBy());
			document.setModifiedBy(users.getModifiedBy() == null ? "" : users.getModifiedBy());
		}
		document.setYear(year);
		Document document2 = documentRepository.findByYearAndDocType(year, docType);
		if (document2 != null) {
			document.setId(document2.getId());
			documentRepository.save(document);
		}
		documentRepository.save(document);

	}
}
