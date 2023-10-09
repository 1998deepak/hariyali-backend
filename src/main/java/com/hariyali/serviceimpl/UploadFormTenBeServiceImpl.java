package com.hariyali.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hariyali.config.JwtHelper;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Users;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.UploadFormTenBeService;
import com.hariyali.utils.CommonService;

@Service
public class UploadFormTenBeServiceImpl implements UploadFormTenBeService {

	@Autowired
	private DonationRepository donationRepository;

	@Autowired
	private UsersRepository usersRepository;


	@Value("${file.path}")
	String FILE_PATH;
	
//	String FILE_PATH = "C:\\Users\\admin\\Desktop\\GCP_MOUNT_AREA";

	@Autowired
	private CommonService commonService;

	@Autowired
	private JwtHelper jwtHelper;

	@Override
	public void uploadFormTenBe(MultipartFile multipartFile, HttpServletRequest request) throws IOException {

		File zipFile = convertMultipartFileToFile(multipartFile);

		try (ZipFile zip = new ZipFile(zipFile)) {
			Enumeration<ZipArchiveEntry> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				if (entry.getName().toLowerCase().endsWith(".pdf")) {
					processPdf(zip, entry, request);
				}
			}
		}

		// Clean up the temporary zip file if necessary
		zipFile.delete();

	}

	private void processPdf(ZipFile zipFile, ZipArchiveEntry entry, HttpServletRequest request) throws IOException {
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
			Users users = null;
			if (panCard != null) {
				users = usersRepository.findByPanCard(panCard);
			}
			extractAndUploadPdf(zipFile, entry, users, request);
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

	private void extractAndUploadPdf(ZipFile zipFile, ZipArchiveEntry entry, Users users, HttpServletRequest request)
			throws IOException {
		try (InputStream inputStream = zipFile.getInputStream(entry)) {
			if ((zipFile != null) && (users != null)) {
				String path = commonService.getDonarFileFilePath(users.getDonorId());
				File outputFile = null;
				if (path != null) {
					outputFile = new File(path, entry.getName().substring(entry.getName().indexOf("/") + 1));
					FileOutputStream fos = new FileOutputStream(outputFile);
					FileCopyUtils.copy(inputStream, fos);
				}
				Users userToken = null;
				if (request != null) {
					String token = request.getHeader("Authorization");
					if (token != null) {
						String userName = jwtHelper.getUsernameFromToken(token.substring(7));
						userToken = this.usersRepository.findByEmailId(userName);
					}

				}
				if (outputFile != null) {
					commonService.saveDocumentDetails("DOCUMENT",
							entry.getName().substring(entry.getName().indexOf("/") + 1), outputFile.toString(), "PDF",
							"PAN", users, userToken);
				}
			}

		}
	}

	@Override
	public void downloadReceipt(String docNo, HttpServletResponse response) {
//		Receipt receipt = donationRepository.findByDocId(docNo)
		//need to Add logic over here
		
		
	}

}
