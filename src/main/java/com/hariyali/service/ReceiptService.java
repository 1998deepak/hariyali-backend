package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.entity.Donation;

public interface ReceiptService {
	
	public String createReceipt(DonationDTO donation) ;
	
	public ApiResponse<Object> getAllReceipt();
	public String generateReceipt(Donation donation);
}
