package com.hariyali.service;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;


public interface UsersService {

	public ApiResponse<Map<String, Object>> getUsers(int pageNo, int pageSize);

	public ApiResponse<UsersDTO> saveUserAndDonationsOffline(JsonNode jsonNode,HttpServletRequest request) throws JsonMappingException, JsonProcessingException;
	
	public ApiResponse<UsersDTO> deleteUserById(int userId) throws CustomException;

	public ApiResponse<Long> getDonorCunt();

	public ApiResponse<UsersDTO> getUserByEmail(String email);

	public ApiResponse<UsersDTO> getUserPersonalDetails(String email);

	public ApiResponse<Object> getAllUsersWithDonarID();
	
	public ApiResponse<Object> getAllUsersWithWebId();
	
	public ApiResponse<Object> getAllDonationOfSpecificUser(String email);
	    
	public ApiResponse<UsersDTO> updateUser(JsonNode jsonNode, String emailId,HttpServletRequest request);

	public ApiResponse<String> verifyOtp(String string, HttpSession session, HttpServletRequest request) throws JsonMappingException, JsonProcessingException;

	public ApiResponse<String> activateAccount(String formData, HttpSession session) throws JsonMappingException, JsonProcessingException;

	String getOtp(String donorId);

	void saveOtp(String donorId, String otp);

	ApiResponse<String> forgetPassword(String formData, HttpSession session)
			throws JsonProcessingException;

	Users findByDonorId(String donorId);
	
	
	ApiResponse<UsersDTO> getUserPersonalDetailsByDonorId(String donorId);

	ApiResponse<String> approvedOnlineDonationOfUser(String formData, HttpServletRequest request)
			throws JsonProcessingException;

	ApiResponse<UsersDTO> saveUserAndDonationsOnline(JsonNode jsonNode, HttpServletRequest request)
			throws JsonProcessingException;

	
	ApiResponse<String> forgetUserPassword(String formData, HttpSession session) throws JsonProcessingException;


}