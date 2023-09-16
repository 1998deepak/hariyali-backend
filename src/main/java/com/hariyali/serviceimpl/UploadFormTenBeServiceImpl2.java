package com.hariyali.serviceimpl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import com.hariyali.entity.Donation;
import com.hariyali.repository.DonationRepository;
import com.hariyali.service.UploadFormTenBeService;

@Service
public class UploadFormTenBeServiceImpl2 implements UploadFormTenBeService {

	@Autowired
	private DonationRepository donationRepository;
	String FILE_PATH = "C:\\Users\\admin\\Desktop\\GCP_MOUNT_AREA";

	@Override
	public void uploadFormTenBe(MultipartFile multipartFile) throws IOException {

		File zipFile = convertMultipartFileToFile(multipartFile);

		try (ZipFile zip = new ZipFile(zipFile)) {
			Enumeration<ZipArchiveEntry> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				if (entry.getName().toLowerCase().endsWith(".pdf")) {
					processPdf(zip, entry);
				}
			}
		}

		// Clean up the temporary zip file if necessary
		zipFile.delete();

	}

	private void processPdf(ZipFile zipFile, ZipArchiveEntry entry) throws IOException {
		InputStream inputStream = zipFile.getInputStream(entry);
		try (PDDocument document = PDDocument.load(inputStream)) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);
			int i, j = 0;
			String temp = "";

			// Get Pan Card Details
			temp = "(b)   Unique Identification Number ";
			i = text.indexOf(temp);
			i += temp.length();
			String panCard = text.substring(i, i + 10).trim();
			Donation donation = null;
			if (panCard != null) {
				donation = donationRepository.findByUserPan(panCard);
			}
			extractAndUploadPdf(zipFile, entry, donation);
			System.out.println("panCard=>" + panCard);

		}

	}

	private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
		File file = File.createTempFile("upload", null);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			FileCopyUtils.copy(multipartFile.getInputStream(), fos);
		}
		return file;
	}

	private void extractAndUploadPdf(ZipFile zipFile, ZipArchiveEntry entry, Donation donation) throws IOException {
		try (InputStream inputStream = zipFile.getInputStream(entry)) {

			if ((zipFile != null) && (donation != null)) {
				String folderName = FILE_PATH + "\\" + new Integer(donation.getDonationId()).toString();
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
				File outputFile = new File(folderName, entry.getName().substring(entry.getName().indexOf("/") + 1));
				FileOutputStream fos = new FileOutputStream(outputFile);
				FileCopyUtils.copy(inputStream, fos);

			}

		}
	}

}
