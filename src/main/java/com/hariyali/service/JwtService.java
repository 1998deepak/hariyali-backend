package com.hariyali.service;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.LoginRequest;

public interface JwtService {

	public ApiResponse<String> login(LoginRequest request) ;

	public ApiResponse<String> resetPassword(String formData) throws JsonProcessingException;
	
	public ApiResponse<String> sendEmailPassword(String email);
	
	public ApiResponse<String> logout(LoginRequest request, String token);

	
}
