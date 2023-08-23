package com.hariyali.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.ReceiptService;
import com.hariyali.utils.Conversion;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Service
public class ReceiptServiceImpl implements ReceiptService {

	@Autowired
	ReceiptRepository receiptRepository;
	@Autowired
	DonationRepository donationRepository;

	@Autowired
	UsersRepository userRepository;

	@Value("${receipt.receiptPath}")
	String receiptPath;

	private String getReceiptNumber() {
		String prefix = "R100";
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int lastTwoDigits = currentYear % 100;
		String yearSuffix = String.format("%02d", lastTwoDigits);
		Random random = new Random();
		StringBuilder randomDigits1 = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			randomDigits1.append(random.nextInt(10)); // Append a random digit (0-9)
		}
		String randomDigits = randomDigits1.toString();

		return prefix + yearSuffix + randomDigits;
	}

	@Override
	public ApiResponse<Object> getAllReceipt() {

		ApiResponse<Object> response = new ApiResponse<>();

		Object result = receiptRepository.getAllReciept();
		if (result != null) {
			response.setData(result);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;
		} else
			throw new CustomException("There is No user who has webId");

	}

	public String generateReceipt(Donation donation) {
		String receiptNo = getReceiptNumber();
		Users users = userRepository.getUserByDonationId(donation.getDonationId());
		String userFolder = receiptPath + "\\" + users.getDonorId() + "_" + users.getFirstName() + "_"
				+ users.getLastName()+ "\\";
		File directory = new File(userFolder);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		String name = users.getFirstName() + " " + users.getLastName();
		String pancard = users.getPanCard();
		String receiptFilename = "Receipt_" + receiptNo + ".pdf";
		String fullPath = userFolder + receiptFilename;
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		String formattedDate = today.format(formatter);
		Document document = new Document(PageSize.A4);
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(fullPath));
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			Font courierFontItalic = new Font(Font.FontFamily.COURIER, 20, Font.ITALIC);
			document.open();
			Paragraph p = new Paragraph("Hariyali", courierFontItalic);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);
			Paragraph title = new Paragraph("K.C. MAHINDRA EDUCATION TRUST", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph("\n"));
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(60);
			PdfPCell cell = new PdfPCell(new Paragraph("Cecil Court, Mahakavi Bushan Marg, Mumbai, 400001."));
			cell.setBorder(Rectangle.BOX);
			cell.setPadding(10);
			table.addCell(cell);
			document.add(table);

			PdfPTable receiptTable = new PdfPTable(3);
			receiptTable.setWidthPercentage(100);

			PdfPCell leftCell = new PdfPCell(new Phrase("NO:" + receiptNo));
			leftCell.setBorder(Rectangle.NO_BORDER);
			receiptTable.addCell(leftCell);

			Font receiptFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
			PdfPCell centerCell = new PdfPCell(new Paragraph("Receipt", receiptFont));
			centerCell.setBorder(Rectangle.NO_BORDER);
			centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			receiptTable.addCell(centerCell);

			PdfPCell rightCell = new PdfPCell(new Phrase("DATE: " + formattedDate));
			rightCell.setBorder(Rectangle.NO_BORDER);
			rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			receiptTable.addCell(rightCell);

			document.add(receiptTable);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));

			System.err.println("amount:" + donation.getTotalAmount());
			Integer roundUpAmount = (int) Math.round(donation.getTotalAmount());
			System.err.println("roundUpAmount:" + roundUpAmount);
			String amountWords = Conversion.NumberToWord(roundUpAmount.toString());
			String amountInWords = Conversion.formattedAmountInWords(amountWords);
			System.err.println("amountInWords:" + amountInWords);
			Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
			Paragraph donorDetails = new Paragraph();
			donorDetails.setAlignment(Element.ALIGN_LEFT);
			Chunk nameChunk = new Chunk("Received with thanks from ");
			donorDetails.add(nameChunk);
			donorDetails.add(new Chunk(name + " (PAN – " + pancard + ")*", boldFont));
			donorDetails.add(" the sum of Rupees " + amountInWords
					+ " only through our Website Dt. "+formattedDate+" towards your donation.");
			document.add(donorDetails);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));

			// Add Amount Details
			PdfPTable receiptTable1 = new PdfPTable(2);
			receiptTable.setWidthPercentage(100);

			PdfPCell amount = new PdfPCell(new Paragraph("INR." + donation.getTotalAmount(), receiptFont));
			amount.setBorder(Rectangle.NO_BORDER);
			amount.setHorizontalAlignment(Element.ALIGN_LEFT);
			amount.setVerticalAlignment(Element.ALIGN_TOP);
			receiptTable1.addCell(amount);

			PdfPCell sign = new PdfPCell(new Paragraph("For K.C. MAHINDRA EDUCATION TRUST", receiptFont));
			sign.setBorder(Rectangle.NO_BORDER);
			sign.setHorizontalAlignment(Element.ALIGN_RIGHT);
			receiptTable1.addCell(sign);
			document.add(receiptTable1);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			Paragraph signParagraph = new Paragraph("(Authorized Signatory)",
					FontFactory.getFont(FontFactory.HELVETICA_BOLD));
			signParagraph.setAlignment(Element.ALIGN_RIGHT);
			document.add(signParagraph);

//		Image signatureImage = Image.getInstance(getClass().getResource("C:/Users/HP/Pictures/I_R_multi_1610868885987.jpg"));
//		signatureImage.scaleToFit(100, 100); // Adjust size as needed
//		signatureImage.setAlignment(Element.ALIGN_RIGHT);
//		document.add(signatureImage);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));

			LineSeparator line = new LineSeparator();
			document.add(line);
			Paragraph additionalText = new Paragraph(
					"Income Tax Exemption U/S 80-G Granted Vide Certificate Number AAATK0315QF2021401 dated 28th May 2021 containing Approval Number AAATK0315QF20214 valid from 01st April 2021 to 31st March 2026. PAN - "
							+ pancard);
			additionalText.setAlignment(Element.ALIGN_LEFT);
			document.add(additionalText);
			Paragraph note = new Paragraph("*Note-\n"
					+ "Kindly note that K.C. Mahindra Education Trust shall not be responsible for the verification of the donor's PAN details as well as for the denial of deduction u/s 80G of the Income Tax Act, 1961 for furnishing an incorrect PAN.",
					receiptFont);
			note.setAlignment(Element.ALIGN_LEFT);
			document.add(note);
			document.close();
			writer.close();
			Receipt receipt = new Receipt();
			Donation dnations = donationRepository.getById(donation.getDonationId());
			receipt.setDonation(dnations);
			receipt.setRecieptDate(dnations.getCreatedDate());
			receipt.setRecieptNumber(receiptNo);
			receipt.setReciept_Path(fullPath);
			receiptRepository.save(receipt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return fullPath;

	}
	
	@Override
	public void downloadReceipt(String recieptNumber, HttpServletResponse response)
			throws IOException {
		Receipt receipt = receiptRepository.getByRecieptNumber(recieptNumber);

		if (receipt == null || receipt.getReciept_Path() == null) {
			// Handle case where receipt is not found or path is not available
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String filePath = receipt.getReciept_Path(); // Get the actual file path from the receipt
		System.out.println("filePath" + filePath);
		File file = new File(filePath);
		if (!file.exists()) {
			// Handle case where file is not found
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Set response headers
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

		// Stream the file content to response output stream
		try (FileInputStream inputStream = new FileInputStream(file)) {
			IOUtils.copy(inputStream, response.getOutputStream());
		}
	}
}
