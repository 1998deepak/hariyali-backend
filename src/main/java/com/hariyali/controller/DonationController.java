package com.hariyali.controller;

import javax.servlet.http.HttpServletRequest;

import com.hariyali.dto.PaymentInfoDTO;
import com.hariyali.service.PaymentIntegrationService;
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
import com.hariyali.dto.ApiRequest;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.entity.Donation;
import com.hariyali.service.DonationService;

@RestController
@RequestMapping("/api/v1")
public class DonationController {

	private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

	@Autowired
	private DonationService donationService;

	@Autowired
	private PaymentIntegrationService integrationService;

//	 method to add user new Donations
	@PostMapping("/newDonation")
	public ResponseEntity<Object> addDonation(@RequestBody String formData, HttpServletRequest request)
			throws JsonProcessingException {
		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(donationService.saveUserDonations(response.getFormData(), null, request),
				HttpStatus.OK);
	}

	// API to get donation by Id
	@GetMapping("/getDonationById/{donationId}")
	public ResponseEntity<ApiResponse<Object>> getDonationByDonationId(@PathVariable String donationId) {

		return new ResponseEntity<>(this.donationService.getDonationById(Integer.parseInt(donationId)), HttpStatus.OK);
	}

	@PutMapping("/updateDonation")
	public ResponseEntity<Object> updateDonation(@RequestBody String formData, HttpServletRequest request)
			throws JsonProcessingException {
		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(donationService.updateUserDonations(response.getFormData(), request),
				HttpStatus.OK);
	}

	@GetMapping("/getAllDonationDoneByUser/{email}")
	public ResponseEntity<ApiResponse<Object>> getAllDonationDoneByUser(@PathVariable String email) {
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
	 * @param paymentId
	 * @return
	 */
	@GetMapping("/searchPaymentByPaymentId/{paymentId}")
	public ApiResponse<PaymentInfoDTO> searchPaymentByPaymentId(@PathVariable int paymentId){
		return integrationService.findPaymentInfoByPaymentInfoId(paymentId);
	}
}
