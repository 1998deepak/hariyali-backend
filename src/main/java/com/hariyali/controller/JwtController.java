package com.hariyali.controller;

import javax.servlet.http.HttpServletRequest;

import com.hariyali.utils.EncryptionDecryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiRequest;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.LoginRequest;
import com.hariyali.entity.OtpModel;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.OtpRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.JwtService;
import com.hariyali.serviceimpl.OtpServiceImpl;

@RestController
@RequestMapping("/api/v1/")
public class JwtController {

	private static final Logger logger = LoggerFactory.getLogger(JwtController.class);
	@Autowired
	private JwtService jwtService;

	@Autowired
	OtpServiceImpl otpService;

	@Autowired
	UsersRepository userRepository;

	@Autowired
	OtpRepository otpRepository;

	@Autowired
	private EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Value("${user.account.locktime}")
	private Integer lockTime;

	@GetMapping("welcome")
	public ResponseEntity<?> welcome() {
		return new ResponseEntity<>("welcome", HttpStatus.OK);
	}

	// method to generate token by passing user-name and password of user
	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
		try {

			ApiResponse<?> response = jwtService.login(loginRequest);

			if ("Error".equals(response.getStatus())) {
				return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Invalid Username or Password", HttpStatus.BAD_REQUEST);
		}

	}

	// two-step verification
	@PostMapping("/loginOtp")
	public ResponseEntity<?> loginOtp(@RequestBody LoginRequest loginRequest) throws Exception {
		try {
			ApiResponse<?> response = jwtService.loginOtp(loginRequest);
			if ("Error".equals(response.getStatus())) {
				return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Invalid Username or Password", HttpStatus.BAD_REQUEST);
		}
	}

	// verify otp
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp1(@RequestParam String donarIdOrEmail, @RequestParam String otp) {
		if (otp == null || donarIdOrEmail == null) {
			throw new CustomExceptionNodataFound("Please enter your Donar Id or Email  and OTP");
		}
		donarIdOrEmail = encryptionDecryptionUtil.decrypt(donarIdOrEmail);
		otp = encryptionDecryptionUtil.decrypt(otp);
		OtpModel otpModel = otpService.findByOtp(otp);
		if (otpModel == null) {
			throw new CustomExceptionNodataFound("Your OTP has been expired... Please resend OTP...");
		}
		Users user = jwtService.findUserByDonorIdOrEmailId(donarIdOrEmail);
		if (user == null || !otp.equals(otpModel.getOtpCode())) {
			return ResponseEntity.badRequest().body("Invalid OTP... Please Enter correct OTP");
		}

		// OTP is valid, perform authentication logic
		ApiResponse<?> response = jwtService.verifyOtp(donarIdOrEmail, otp);
		otpModel.setOtpCode(null);
		otpModel.setOtpExpiryTime(null);
		otpRepository.save(otpModel);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/reSendOtp")
	public ResponseEntity<?> reSendOtp(@RequestParam String donarIdOrEmail) throws Exception {
		ApiResponse<?> result = new ApiResponse<>();
		try {
			otpService.sendOtpByEmail(donarIdOrEmail);
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Otp Send Successfully");
			result.setStatusCode(HttpStatus.OK.value());
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Invalid Mail", HttpStatus.BAD_REQUEST);
		}
	}

	// method to send email for reseting password
	@PostMapping("sendEmail")
	public ResponseEntity<?> sendEmailResetPassword(@RequestParam String email) {
		try {
			ApiResponse res = this.jwtService.sendEmailPassword(email);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred - ", e);
			return new ResponseEntity<>("Something Went Wrong", HttpStatus.BAD_REQUEST);
		}
	}

	// method to logout
	@PostMapping("logout")
	public ResponseEntity<?> logout(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		logger.info("logout method called Successfully..");
		String token = request.getHeader("Authorization").substring(7);
		try {
			ApiResponse res = this.jwtService.logout(loginRequest, token);
			logger.debug("Logged out successfully - {}", loginRequest.getUsername());
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred - ", e);
			return new ResponseEntity<>("Something Went Wrong", HttpStatus.BAD_REQUEST);
		}
	}

	// method to reset password
	@PostMapping("resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody String formData) throws JsonProcessingException {

		ApiRequest apiRequest = new ApiRequest(formData);

		return new ResponseEntity<>(this.jwtService.resetPassword(apiRequest.getFormData().toString()), HttpStatus.OK);

	}

}
