package com.hariyali.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hariyali.dto.ApiRequest;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.LoginRequest;
import com.hariyali.service.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/")
public class JwtController {

	private static final Logger logger = LoggerFactory.getLogger(JwtController.class);
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

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
		
		ApiRequest apiRequest=new ApiRequest(formData);
		
			return new ResponseEntity<>(this.jwtService.resetPassword(apiRequest.getFormData().toString()), HttpStatus.OK);
		
	}
}
