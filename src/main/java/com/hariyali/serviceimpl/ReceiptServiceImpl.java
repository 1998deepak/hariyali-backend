package com.hariyali.serviceimpl;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.ReceiptDto;
import com.hariyali.entity.Address;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.ReceiptService;
import com.hariyali.utils.Conversion;
import com.hariyali.utils.EmailService;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    DonationRepository donationRepository;

    @Autowired
    UsersRepository userRepository;

    @Autowired
    EmailService emailService;

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
			response.setMessage("Data fetched successfully..");
			return response;
		} else
			throw new CustomException("There is No user who has webId");

    }

    public String generateReceipt(Donation donation) {
        String receiptNo = getReceiptNumber();
        Users users = userRepository.getUserByDonationId(donation.getDonationId());
        String userFolder = receiptPath + "\\" + users.getEmailId() + "\\";
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
            Font normal = new Font(Font.FontFamily.HELVETICA, 10);
            writer = PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            Image logo = null;
            try {
//                				logo = Image.getInstance("src/main/resources/Logo.png");
                Path path = emailService.getFileFromPath("Logo.png");
                System.out.println("Logo=>" + path.toString());
                logo = Image.getInstance(path.toString());
                logo.scaleToFit(600, 50); // Adjust the size as needed
                logo.setAlignment(Element.ALIGN_CENTER);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create a Paragraph to hold the logo
            Paragraph logoParagraph = new Paragraph();
            logoParagraph.setAlignment(Element.ALIGN_CENTER);
            logoParagraph.add(logo);

            // Add the logo to the document
            document.add(logoParagraph);
//            document.add(new Paragraph("\n"));
            PdfPTable receiptTable = new PdfPTable(2);
            receiptTable.setWidthPercentage(100);


            Font receiptFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph receiptParagraph = new Paragraph();
            receiptParagraph.setAlignment(Element.ALIGN_CENTER);
            receiptParagraph.add("Receipt");
            receiptParagraph.setFont(receiptFont);
            document.add(receiptParagraph);

//            document.add(new Paragraph("\n"));

            PdfPCell leftCell = new PdfPCell(new Phrase("Transaction Number:" + donation.getOrderId()));
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setBorderWidth(50);
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            receiptTable.addCell(leftCell);

            PdfPCell rightCell = new PdfPCell(new Phrase("DATE: " + formattedDate));
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setBorderWidth(50);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            receiptTable.addCell(rightCell);

            leftCell = new PdfPCell(new Phrase("Donation Code:" + donation.getDonationCode()));
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setBorderWidth(50);
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            receiptTable.addCell(leftCell);


            rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setBorderWidth(50);
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
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Paragraph donorDetails = new Paragraph();
            donorDetails.setAlignment(Element.ALIGN_LEFT);
            Chunk nameChunk = new Chunk("Received with thanks from ", normal);
            donorDetails.add(nameChunk);
            donorDetails.add(new Chunk(name.toUpperCase(), normal));
            donorDetails.add(new Chunk(" the sum of Rupees ",normal));
            donorDetails.add(new Chunk(amountInWords, normal));
            donorDetails.add(new Chunk(" only through our Website Dt. " + formattedDate + " towards your donation.",normal));
            document.add(donorDetails);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            BigDecimal donationAmount = BigDecimal.valueOf(donation.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);
            System.err.println("donationAmount:" + donationAmount);
            PdfPTable informationTable = new PdfPTable(2);
            informationTable.setWidthPercentage(100);
            var paragraph = new Paragraph();
            paragraph.add("\n\nINR " + donationAmount);
            paragraph.add("\nFor Naandi Foundation\n\n");

            Image logoSeal = null;
            try {
                Path path = emailService.getFileFromPath("sealLogo.jpg");
                System.out.println("Logo=>" + path.toString());
                logoSeal = Image.getInstance(path.toString());
                logoSeal.scaleToFit(65, 65);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            paragraph.add(logoSeal);

            PdfPCell leftInfoCell = new PdfPCell();
            leftInfoCell.addElement(paragraph);
            leftInfoCell.addElement(logoSeal);
            leftInfoCell.setBorder(Rectangle.NO_BORDER);
            leftInfoCell.setBorderWidth(1);
//            leftInfoCell.setRowspan(2);
            leftInfoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            informationTable.addCell(leftInfoCell);

            Address address = donation.getUsers().getAddress().get(0);
            var p = new Paragraph();
            p.add("Donors Information:\n");
            p.add("Address: " + address.getStreet1()+"\n");
            if (StringUtils.isNotEmpty(address.getStreet1())) {
                p.add(address.getStreet1() + "\n");
            }
            if (StringUtils.isNotEmpty(address.getStreet2())) {
                p.add(address.getStreet2() + "\n");
            }
            if (StringUtils.isNotEmpty(address.getStreet3())) {
                p.add(address.getStreet3() + "\n");
            }
            p.add("State: " + address.getState());

            p.add("\nCountry: " + address.getCountry());

            p.add("\nPin Code/Postal Code: " + address.getPostalCode());

            p.add("\nPAN/AADHAAR:  " + ofNullable(donation.getUsers().getPanCard()).filter(pan -> !pan.isEmpty()).orElse(donation.getUsers().getAadharCard()));

            PdfPCell rightInfoCell = new PdfPCell(p);
            rightInfoCell.setBorder(Rectangle.BOX);
            rightInfoCell.setBorderWidth(1);
            rightInfoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            rightInfoCell.setPadding(10f);
//            rightInfoCell.setRowspan(12);
            informationTable.addCell(rightInfoCell);

//            document.add(new Paragraph("\n"));
//            leftInfoCell = new PdfPCell(logoSeal);
//            leftInfoCell.setBorder(Rectangle.NO_BORDER);
//            leftInfoCell.setBorderWidth(1);
//            leftInfoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            informationTable.addCell(leftInfoCell);
//            rightInfoCell = new PdfPCell();
//            rightInfoCell.setBorder(Rectangle.NO_BORDER);
//            rightInfoCell.setBorderWidth(1);
////            rightInfoCell.setRowspan(1);
//            rightInfoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            informationTable.addCell(rightInfoCell);
            document.add(informationTable);

            Chunk c2 = new Chunk("(This is a computer-generated receipt, signature not required)", normal);
            Paragraph lastPara3 = new Paragraph();
            lastPara3.add(c2);
            lastPara3.setAlignment(Element.ALIGN_CENTER);
            lastPara3.setSpacingAfter(15f);
            lastPara3.setSpacingBefore(10f);
            document.add(lastPara3);
//            document.add(new Paragraph("\n"));

            PdfPTable noteTable = new PdfPTable(1);
            noteTable.setWidthPercentage(100);
            Phrase additionalText = new Phrase(
                    "Donation made to Naandi Foundation qualifies for deduction U/s. 80(G)(5)(i) of Income Tax Act,\n 1961 vide Order for Approval granted with reference number AAATN2405LF20214 dated 31-05-2021 \r\n"
                            + "and \nDocument Identification Number AAATN2405LF2021401", normal);
            PdfPCell noteCell1 = new PdfPCell(additionalText);
            noteCell1.setBorder(Rectangle.BOX);
            noteCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            noteCell1.setBorderWidth(1);
            noteCell1.setPadding(10f);
            noteTable.addCell(noteCell1);

            additionalText = new Phrase(
                    "PAN : AAATN2405L", normal);
            noteCell1 = new PdfPCell(additionalText);
            noteCell1.setBorder(Rectangle.BOX);
            noteCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            noteCell1.setBorderWidth(1);
            noteCell1.setPadding(5f);
            noteTable.addCell(noteCell1);

            document.add(noteTable);
//            document.add(new Paragraph("\n"));

            Paragraph note = new Paragraph("This receipt is issued for accounting purpose only");
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
            document.add(new Paragraph("\n"));

            note = new Paragraph("We shall issue Form 10BE after completion of the financial year as per CBDT notification no. 19/2021 dated 26.03.2021");
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
            document.add(new Paragraph("\n"));

            note = new Paragraph("Note- Kindly note that Naandi Foundation shall not be responsible for the verification of the donor's PAN details as well as for the denial of deduction u/s 80G of the Income Tax Act, 1961 for furnishing an incorrect PAN.", boldFont);
            note.setAlignment(Element.ALIGN_LEFT);
//            note.setFont(boldFont);
            document.add(note);
            document.add(new Paragraph("\n"));

            note = new Paragraph("Naandi Foundation 502, Trendset Towers, Road No. 2, Banjara Hills, Hyderabad – 500 034, ");
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
//            document.add(new Paragraph("\n"));
            note = new Paragraph("Telangana INDIA");
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
//            document.add(new Paragraph("\n"));
            note = new Paragraph("E-Mail: support@hariyali.org.in");
            note.setAlignment(Element.ALIGN_CENTER);
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
    public void downloadReceipt(String recieptNumber, HttpServletResponse response) throws IOException {
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

    @Override
    public List<ReceiptDto> getAllReceipt(String emailId) {

        List<Receipt> result = receiptRepository.getAllReciept(emailId);
        List<ReceiptDto> receiptDTOList = new ArrayList<>();

        if (result != null) {
            for (Receipt receipt : result) {
                ReceiptDto receiptDTO = new ReceiptDto();
                receiptDTO.setReceiptId(receipt.getRecieptId());
                receiptDTO.setReciept_number(receipt.getRecieptNumber());
                receiptDTO.setRecieptDate(receipt.getRecieptDate());
                receiptDTO.setReciept_Path(receipt.getReciept_Path());
                receiptDTO.setDonation_id(receipt.getDonation().getDonationId());
                receiptDTOList.add(receiptDTO);
            }
            return receiptDTOList;
        } else {
            throw new CustomException("There is no user with the provided emailId");
        }
    }
}
