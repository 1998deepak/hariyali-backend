//package com.hariyali.serviceimpl;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.hariyali.entity.Donation;
//import com.hariyali.repository.DonationRepository;
//import com.hariyali.service.UploadFormTenBeService;
//
//@Service
////public class UploadFormTenBeServiceImpl implements UploadFormTenBeService {
//
//	@Autowired
//	private DonationRepository donationRepository;
//	String FILE_PATH = "C:\\Users\\admin\\Desktop\\GCP_MOUNT_AREA";
//
////	@Value("${spring.file.path}")
////	String uploadpathNmi;
//	@Override
//	public void uploadFormTenBe(MultipartFile multipartFile) {
//
//		try (InputStream zipInputStream = new ByteArrayInputStream(multipartFile.getBytes());
//				ZipInputStream zis = new ZipInputStream(zipInputStream)) {
//
//			ZipEntry entry;
//			try {
//				while ((entry = zis.getNextEntry()) != null) {
//					if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".pdf")) {
////						String fileSize = fileSizeValidate(entry, 10);
////						System.out.println("fileSize=>" + fileSize);
//						processPdf(zis, entry);
//						
//					}
//					zis.closeEntry();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		 File zipFile = convertMultipartFileToFile(file);
//
//	}
//
//	private void processPdf(InputStream pdfInputStream, ZipEntry entry) throws IOException {
//		try (PDDocument document = PDDocument.load(pdfInputStream)) {
//			PDFTextStripper pdfStripper = new PDFTextStripper();
//			String text = pdfStripper.getText(document);
//			int i, j = 0;
//			String temp = "";
//
//			// Get Pan Card Details
//			temp = "(b)   Unique Identification Number ";
//			i = text.indexOf(temp);
//			i += temp.length();
//			String panCard = text.substring(i, i + 10).trim();
//			Donation donation = null;
//			if (panCard != null) {
//				donation = donationRepository.findByUserPan(panCard);
//			}
//			uploadFileIntoSpecificFolder(donation, pdfInputStream, entry, readInputStream(pdfInputStream));
//			System.out.println("panCard=>" + panCard);
//
//		}
//	}
//
//	private void uploadFileIntoSpecificFolder(Donation donation, InputStream pdfInputStream, ZipEntry entry,
//			byte[] data) throws IOException {
//		if ((pdfInputStream != null) /* && (donation != null) */) {
//			String folderName = FILE_PATH + "\\" /* + new Integer(donation.getDonationId()).toString() */;
//			// Create the folder
//			Path folder = Paths.get(folderName);
//			System.out.println("folder Path=>" + folder);
//			if (!Files.exists(folder)) {
//				try {
//					Files.createDirectory(folder);
//					System.out.println("Folder created successfully.");
//				} catch (IOException e) {
//					System.err.println("Failed to create folder: " + e.getMessage());
//				}
//			} else {
//				System.out.println("Folder already exists.");
//			}
//			System.out.println("Name=>" + entry.getName());
//			String str = entry.getName().substring(entry.getName().indexOf("/") + 1);
//			Path path = Paths.get(folder + "\\" + str);
//			System.out.println("Path=" + path);
//			System.out.println("data size=>" + data.length);
//			Files.write(path, data);
//			// Copy the contents of the input stream to the output file
////	        Files.copy(data, path, StandardCopyOption.REPLACE_EXISTING);
//
//		}
//
//	}
//
//	public static String fileSizeValidate(ZipEntry entry, int size) {
//		if (entry != null) {
//			Double size_mb = entry.getSize() * 0.00000095367432;
//			if (size_mb > size)
//				return "EXCEEDSIZE";
//		}
//		return "CONTINUE";
//	}
//
//	private static byte[] readInputStream(InputStream inputStream) throws IOException {
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		byte[] buffer = new byte[20971520];
//		int bytesRead;
//		while ((bytesRead = inputStream.read(buffer)) != -1) {
//			outputStream.write(buffer, 0, bytesRead);
//		}
//		return outputStream.toByteArray();
//	}
//
//}
