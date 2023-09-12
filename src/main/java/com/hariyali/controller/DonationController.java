package com.hariyali.controller;

import javax.servlet.http.HttpServletRequest;

import com.hariyali.dto.*;
import com.hariyali.service.PaymentIntegrationService;
import com.hariyali.utils.EncryptionDecryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hariyali.entity.Donation;
import com.hariyali.service.DonationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DonationController {

	private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

	@Autowired
	private DonationService donationService;

	@Autowired
	private PaymentIntegrationService integrationService;

	@Autowired
	private EncryptionDecryptionUtil encryptionDecryptionUtil;

//	 method to add user new Donations
	@PostMapping("/newDonation")
	public ResponseEntity<Object> addDonation(@RequestBody UsersDTO formData, HttpServletRequest request)
			throws JsonProcessingException {
		return new ResponseEntity<>(donationService.saveUserDonations(formData, null, request),
				HttpStatus.OK);
	}

	// API to get donation by Id
	@GetMapping("/getDonationById/{donationId}")
	public ResponseEntity<ApiResponse<Object>> getDonationByDonationId(@PathVariable String donationId) {
		donationId = encryptionDecryptionUtil.decrypt(donationId);
		return new ResponseEntity<>(this.donationService.getDonationById(Integer.parseInt(donationId)), HttpStatus.OK);
	}

	@PutMapping("/updateDonation")
	public ResponseEntity<Object> updateDonation(@RequestBody UsersDTO formData, HttpServletRequest request)
			throws JsonProcessingException {
//		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(donationService.updateUserDonations(formData, request),
				HttpStatus.OK);
	}

	@GetMapping("/getAllDonationDoneByUser/{email}")
	public ResponseEntity<ApiResponse<Object>> getAllDonationDoneByUser(@PathVariable String email) {
//		email = encryptionDecryptionUtil.decrypt(email);
		ApiResponse<Object> apiResponse = donationService.getAllDonationDoneByUser(email);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping("/searchDonationById/{donationId}")
	public ResponseEntity<ApiResponse<DonationDTO>> searchDonationById(@PathVariable int donationId){
		return new ResponseEntity<>(this.donationService.searchDonationById(donationId), HttpStatus.OK);
	}
	
	@GetMapping("/searchDonationById1/{donationId}")
	public Donation searchDonationById1(@PathVariable int donationId){
		return donationService.searchDonationById1(donationId);
	}

	/**
	 * method to search payment information based on payment id
	 *
	 * @param orderId
	 * @return
	 */
	@GetMapping("/searchPaymentByPaymentId/{orderId}")
	public ApiResponse<PaymentInfoDTO> searchPaymentByPaymentId(@PathVariable String orderId){
		orderId = encryptionDecryptionUtil.decrypt(orderId);
		return integrationService.findPaymentInfoByOrderId(orderId);
	}

	/**
	 * Rest endpoint to get user donation list
	 *
	 * @param requestDTO
	 * @return
	 */
	@PostMapping("/getUserDonations")
	public ApiResponse<List<DonationDTO>> searchDonationById1(@RequestBody DonorListRequestDTO requestDTO){
		return donationService.getDonations(requestDTO);
	}

}
