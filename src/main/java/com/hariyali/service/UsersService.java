package com.hariyali.service;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonorListRequestDTO;
import com.hariyali.dto.LoginRequest;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;


public interface UsersService {

	public ApiResponse<Map<String, Object>> getUsers(int pageNo, int pageSize);

	public ApiResponse<UsersDTO> saveUserAndDonationsOffline(UsersDTO usersDTO,HttpServletRequest request) throws JsonMappingException, JsonProcessingException, MessagingException;
	
	public ApiResponse<UsersDTO> deleteUserById(int userId) throws CustomException;

	public ApiResponse<Long> getDonorCunt();

	public ApiResponse<UsersDTO> getUserByEmail(String email);

	public ApiResponse<UsersDTO> getUserPersonalDetails(String email);
	
	public ApiResponse<UsersDTO> getExistingUserByEmail(String email);

	public ApiResponse<Object> getAllUsersWithDonarID();
	
	public ApiResponse<List<UsersDTO>> getAllUsersWithWebId(DonorListRequestDTO requestDTO);
	
	public ApiResponse<Object> getAllDonationOfSpecificUser(String email);
	    
	public ApiResponse<UsersDTO> updateUser(UsersDTO usersDTO, String emailId,HttpServletRequest request);

//	public ApiResponse<String> verifyForgotOtp(String string, HttpSession session, HttpServletRequest request) throws JsonMappingException, JsonProcessingException;

	public ApiResponse<String> verifyForgotOtp(String email, String otp);
	
	public ApiResponse<String> activateAccount(String formData, HttpSession session) throws JsonMappingException, JsonProcessingException;

	String getOtp(String donorId);

	void saveOtp(String donorId, String otp);

	ApiResponse<String> forgetPassword(String donorId, HttpSession session)
			throws JsonProcessingException;

	Users findByDonorId(String donorId);
	
	
	ApiResponse<UsersDTO> getUserPersonalDetailsByDonorId(String donorId);

	ApiResponse<String> approvedOnlineDonationOfUser(UsersDTO usersDTO, HttpServletRequest request)
			throws JsonProcessingException, MessagingException;

	ApiResponse<UsersDTO> saveUserAndDonationsOnline(UsersDTO usersDTO, HttpServletRequest request)
			throws JsonProcessingException;

	
	ApiResponse<String> setUserNewPassword(LoginRequest loginRequest, HttpSession session) throws JsonProcessingException;

	public ApiResponse<UsersDTO> getUserPersonalDetailsbyEmailOrDonorId(String emailOrDonorId);

	public List<String> getAllDonarId();

	ApiResponse<String> getUserDonarId(String email);
	ApiResponse<String> changePassword(LoginRequest request, String token);

	List<String> getAllUserIds();

	ApiResponse<List<Donation>> getUserDonations(String email, Integer pageNo, Integer pageSize);

	public ByteArrayInputStream downloadDonationReport(DonorListRequestDTO requestDTO);
}