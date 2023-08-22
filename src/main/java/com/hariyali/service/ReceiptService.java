package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Donation;

public interface ReceiptService {

	public ApiResponse<Object> getAllReceipt();

	public String generateReceipt(Donation donation);
}
