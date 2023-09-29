package com.hariyali.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.dto.DonorListRequestDTO;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Donation;

public interface DonationService {

	public ApiResponse<Object> getDonationById(int donationId);

	public Object saveUserDonations(UsersDTO usersDTO, String donarID, HttpServletRequest request) throws JsonMappingException, JsonProcessingException;

	public Object updateUserDonations(UsersDTO usersDTO, HttpServletRequest request);

	public ApiResponse<Donation> getDonation(int donationId);

	public ApiResponse<Object> getAllDonationDoneByUser(String email);
	
	public ApiResponse<DonationDTO> searchDonationById(int donationId);
	
	public Donation searchDonationById1(int donationId);

	/**
	 * Get all user donation by donation id
	 *
	 * @param requestDTO
	 * @return
	 */
	public ApiResponse<List<DonationDTO>> getDonations(DonorListRequestDTO requestDTO);
	
	public Map<String,String> generateCertificate(String recipientName ,String messageContent,String donationEvent,String donarName,String emailID);

}
