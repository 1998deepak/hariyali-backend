package com.hariyali.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hariyali.dto.ApiRequest;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.service.DonationService;
import com.hariyali.service.UsersService;

@RestController
@RequestMapping("/api/v1")
public class UsersController {

	@Autowired
	private UsersService usersService;

	// method to get user by email
	@GetMapping("/getAlluser")
	public ResponseEntity<ApiResponse<Object>> getAllusers() {
		return new ResponseEntity<>(usersService.getAllUsersWithDonarID(), HttpStatus.OK);
	}

	// method to get user by email
	@GetMapping("/getuser/{email}")
	public ResponseEntity<ApiResponse<UsersDTO>> getByUserEmail(@PathVariable String email) {
		return new ResponseEntity<>(usersService.getUserByEmail(email), HttpStatus.OK);
	}

	// method to delete user
	@DeleteMapping("/deleteuser/{userId}")
	public ResponseEntity<ApiResponse<UsersDTO>> deleteUserById(@PathVariable String userId)
			throws NumberFormatException, CustomException {
		return new ResponseEntity<>(usersService.deleteUserById(Integer.parseInt(userId)), HttpStatus.OK);

	}

	// method to get total number of donors
	@GetMapping("/TotalNoOfDonors")
	public ResponseEntity<ApiResponse<Long>> getDonorCount() {
		return new ResponseEntity<>(this.usersService.getDonorCunt(), HttpStatus.OK);
	}
	
	// method to add user package
	@PostMapping("/userAddOffline")
	public ResponseEntity<ApiResponse<UsersDTO>> addUserOffline(@RequestBody String formData, HttpServletRequest request)
			throws JsonProcessingException {
		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.saveUserAndDonationsOffline(response.getFormData(), request), HttpStatus.OK);

	}
	
	// method to add user package
		@PostMapping("/userAddOnline")
		public ResponseEntity<ApiResponse<UsersDTO>> addUserOnline(@RequestBody String formData, HttpServletRequest request)
				throws JsonProcessingException {
			ApiRequest response = new ApiRequest(formData);
			return new ResponseEntity<>(usersService.saveUserAndDonationsOnline(response.getFormData(), request), HttpStatus.OK);

		}

	// method to get all donation of specific user by email
	@GetMapping("/getAllDonationOfUser/{email}")
	public ResponseEntity<ApiResponse<Object>> getAllDonationOfUser(@PathVariable String email) {
		ApiResponse<Object> apiResponse = this.usersService.getAllDonationOfSpecificUser(email);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping("/updateUser")
	public ResponseEntity<ApiResponse<UsersDTO>> updateUser(@RequestBody String formData, @RequestParam String emailId,
			HttpServletRequest request) throws JsonProcessingException {
		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.updateUser(response.getFormData(), emailId, request), HttpStatus.OK);

	}
	
	// method to get user details by email
	@GetMapping("/getUserDetails/{email}")
	public ResponseEntity<ApiResponse<UsersDTO>> getUserPersonalDetails(@PathVariable String email) {

		return new ResponseEntity<>(usersService.getUserPersonalDetails(email), HttpStatus.OK);
	}

	
	// method to get user personal details by donorId
		@GetMapping("/getUserDetailsByDonorId/{donorId}")
		public ResponseEntity<ApiResponse<UsersDTO>> getUserPersonalDetailsByDonorId(@PathVariable String donorId) {

			return new ResponseEntity<>(usersService.getUserPersonalDetailsByDonorId(donorId), HttpStatus.OK);
		}

	@PostMapping("/verify")
	public ResponseEntity<?> verifyOtp(@RequestParam String donorId, @RequestParam String otp) {
		Users user = usersService.findByDonorId(donorId);
		if (user == null) {
			return ResponseEntity.badRequest().body("Invalid donor ID");
		}
		// Get OTP from cache and compare with input OTP
		String cachedOtp = usersService.getOtp(donorId);
		if (cachedOtp == null || !cachedOtp.equals(otp)) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}
		// OTP is valid, clear it from cache
		usersService.saveOtp(donorId, null);
		return ResponseEntity.ok("OTP verified successfully");
	}

	@PostMapping("/forgetPassword")
	public ResponseEntity<?> forgetPassword(@RequestBody String formData, HttpSession session)
			throws JsonProcessingException {
		ApiRequest apiRequest = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.forgetPassword(apiRequest.getFormData().toString(), session),
				HttpStatus.OK);
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody String formData, HttpSession session,HttpServletRequest request)
			throws JsonProcessingException {
		System.out.println("formData = " + formData);

		ApiRequest apiRequest = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.verifyOtp(apiRequest.getFormData().toString(), session,request),
				HttpStatus.OK);
	}

	@PostMapping("/accountActivate")
	public ResponseEntity<ApiResponse<String>> accountActivate(@RequestBody String formData, HttpSession session)
			throws JsonProcessingException {
		System.out.println("formData = " + formData);

		ApiRequest apiRequest = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.activateAccount(apiRequest.getFormData().toString(), session),
				HttpStatus.OK);
	}

	// method to get user by email
	@GetMapping("/getAlluserWithWebId")
	public ResponseEntity<ApiResponse<Object>> getAllusersWithWebId() {
		return new ResponseEntity<>(usersService.getAllUsersWithWebId(), HttpStatus.OK);
	}
	
	
	@PostMapping("/approvedDonation")
    public ResponseEntity<?> forgetUserPasswordByEmail(@RequestBody String formData,HttpServletRequest request) throws JsonProcessingException {
    	ApiRequest apiRequest=new ApiRequest(formData);	    	
        return new ResponseEntity<>(this.usersService.approvedOnlineDonationOfUser(apiRequest.getFormData().toString(),request),HttpStatus.OK);
    }
	
	@PostMapping("forgetUserPassword")
    public ResponseEntity<?> forgetUserPasswordByEmail(@RequestBody String formData,HttpSession session) throws JsonProcessingException {
    	ApiRequest apiRequest=new ApiRequest(formData);	    	
        return new ResponseEntity<>(this.usersService.forgetUserPassword(apiRequest.getFormData().toString(),session),HttpStatus.OK);
    }

}
