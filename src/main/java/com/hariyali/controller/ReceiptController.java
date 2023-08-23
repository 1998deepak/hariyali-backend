package com.hariyali.controller;

import java.io.IOException;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Receipt;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.service.ReceiptService;

@RestController
@RequestMapping("/api/v1")
public class ReceiptController {

	@Autowired
	ReceiptService receiptService;
	@Autowired
	private JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;

	@Autowired
	ReceiptRepository receiptRepository;

	// method to get All Receipt
	@GetMapping("/getAllReceipt")
	public ResponseEntity<ApiResponse<Object>> getAllReceipt() {
		return new ResponseEntity<>(receiptService.getAllReceipt(), HttpStatus.OK);
	}

	@PostMapping("/send")
	public ResponseEntity<String> sendEmailWithAttachment(@RequestParam("to") String to,
			@RequestParam("subject") String subject, @RequestParam("text") String text,
			@RequestParam("receiptId") int donationId) {

		try {
			// Fetch the receipt from the database
			Receipt receipt = receiptRepository.getReceiptByDonation(donationId);
//			if (!optionalReceipt.isPresent()) {
//				return ResponseEntity.badRequest().body("Receipt not found.");
//			}
//			Receipt receipt = optionalReceipt.get();

			// Create and send the email with attachment
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);

			// Attach the file to the email
			String fileLocation = receipt.getReciept_Path();
			FileSystemResource fileResource = new FileSystemResource(fileLocation);
			helper.addAttachment(fileResource.getFilename(), fileResource, "application/pdf");

			javaMailSender.send(message);

			return ResponseEntity.ok("Email sent successfully with attachment.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error sending email: " + e.getMessage());
		}
	}
	
	@GetMapping("/receipt/download/{recieptNumber}")
	public void downloadReceipt(@PathVariable String recieptNumber, HttpServletResponse response) throws IOException {
		receiptService.downloadReceipt(recieptNumber, response);
	}
}
