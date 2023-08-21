package com.hariyali.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.entity.Donation;

public interface DonationService {

	public ApiResponse<Object> getDonationById(int donationId);

	public Object saveUserDonations(JsonNode formData, String donarID,HttpServletRequest request) throws JsonMappingException, JsonProcessingException;

	public Object updateUserDonations(JsonNode formData, HttpServletRequest request);

	public ApiResponse<Donation> getDonation(int donationId);

	public ApiResponse<Object> getAllDonationDoneByUser(String email);
	
	public ApiResponse<DonationDTO> searchDonationById(int donationId);
	
	public Donation searchDonationById1(int donationId);
}
