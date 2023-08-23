package com.hariyali.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Donation;


public interface ReceiptService {

	public ApiResponse<Object> getAllReceipt();

	public String generateReceipt(Donation donation);

	public void downloadReceipt(String recieptNumber, HttpServletResponse response)
			throws FileNotFoundException, IOException;
}
