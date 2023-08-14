package com.hariyali.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.LoginRequest;
import com.hariyali.entity.Users;

public interface JwtService {

	public ApiResponse<String> login(LoginRequest request) ;

	public ApiResponse<String> resetPassword(String formData) throws JsonProcessingException;
	
	public ApiResponse<String> sendEmailPassword(String email);
	
	public ApiResponse<String> logout(LoginRequest request, String token);

	ApiResponse<String> loginOtp(LoginRequest request);
	
	public ApiResponse<String> verifyOtp(String email, String otp);

	public Users findUserByDonorIdOrEmailId(String email);

	
}
