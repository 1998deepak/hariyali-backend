package com.hariyali.controller;

import java.io.IOException;
import java.util.List;

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

import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.ReceiptDto;
import com.hariyali.entity.Receipt;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.service.ReceiptService;

@RestController
@RequestMapping("/api/v1")
public class ReceiptController {

	@Autowired
	ReceiptService receiptService;

	@Autowired
	ReceiptRepository receiptRepository;

	// method to get All Receipt
	@GetMapping("/getAllReceipt")
	public ResponseEntity<ApiResponse<Object>> getAllReceipt() {
		return new ResponseEntity<>(receiptService.getAllReceipt(), HttpStatus.OK);
	}

	@GetMapping("/receipt/download/{recieptNumber}")
	public void downloadReceipt(@PathVariable String recieptNumber, HttpServletResponse response) throws IOException {
		receiptService.downloadReceipt(recieptNumber, response);
	}

	@GetMapping("/getAllReceiptByUser")
	public ApiResponse<List<ReceiptDto>> getAllReceiptByUser(@RequestParam String emailId) {
		List<ReceiptDto> receiptDTOList = receiptService.getAllReceipt(emailId);

		ApiResponse<List<ReceiptDto>> response = new ApiResponse<>();
		response.setData(receiptDTOList);
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Data fetched successfully..!!");

		return response;
	}
}
