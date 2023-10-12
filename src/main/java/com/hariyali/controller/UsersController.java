package com.hariyali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hariyali.EnumConstants;
import com.hariyali.config.JwtHelper;
import com.hariyali.dto.*;
import com.hariyali.entity.Document;
import com.hariyali.entity.Donation;
import com.hariyali.entity.OtpModel;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.OtpRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.JwtService;
import com.hariyali.service.UsersService;
import com.hariyali.serviceimpl.OtpServiceImpl;
import com.hariyali.utils.CommonService;
import com.hariyali.utils.EncryptionDecryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UsersController {

	@Autowired
	private UsersService usersService;

	@Autowired
	JwtService jwtService;

	@Autowired
	UsersRepository userRepository;

	@Autowired
	OtpServiceImpl otpService;

	@Autowired
	OtpRepository otpRepository;

	@Autowired
	private EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private CommonService commonService;


	// method to get user by email
	@GetMapping("/getAlluser")
	public ResponseEntity<ApiResponse<Object>> getAllusers() {
		return new ResponseEntity<>(usersService.getAllUsersWithDonarID(), HttpStatus.OK);
	}

	// method to get user by email
	@GetMapping("/getuser/{email}")
	public ResponseEntity<ApiResponse<UsersDTO>> getByUserEmail(@PathVariable String email) {
		email = encryptionDecryptionUtil.decrypt(email);
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
	public ResponseEntity<ApiResponse<UsersDTO>> addUserOffline(@RequestBody UsersDTO usersDTO,
			HttpServletRequest request) throws JsonProcessingException, MessagingException {
//		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.saveUserAndDonationsOffline(usersDTO, request), HttpStatus.OK);

	}

	// method to add user package
	@PostMapping("/userAddOnline")
	public ResponseEntity<ApiResponse<UsersDTO>> addUserOnline(@RequestBody UsersDTO formData,
			HttpServletRequest request) throws JsonProcessingException {
		return new ResponseEntity<>(usersService.saveUserAndDonationsOnline(formData, request), HttpStatus.OK);
	}

	// method to get all donation of specific user by email
	@GetMapping("/getAllDonationOfUser")
	public ResponseEntity<ApiResponse<List<Donation>>> getAllDonationOfUser(HttpServletRequest request,@RequestParam(value="email",required=false) String email, @RequestParam("PageSize") Integer pageSize, @RequestParam("PageNo") Integer pageNo) {
		email = Optional.ofNullable(email).orElse(jwtHelper.getUsernameFromToken(request.getHeader("Authorization").substring(7)));
		ApiResponse<List<Donation>> apiResponse = this.usersService.getUserDonations(email, pageNo, pageSize);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping("/updateUser")
	public ResponseEntity<ApiResponse<UsersDTO>> updateUser(@RequestBody UsersDTO formData,
			@RequestParam String emailId, HttpServletRequest request) throws JsonProcessingException {
//		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(usersService.updateUser(formData, emailId, request), HttpStatus.OK);

	}

	// method to get user details by email
	@GetMapping("/getUserDetails/{email}")
	public ResponseEntity<ApiResponse<UsersDTO>> getUserPersonalDetails(@PathVariable String email) {
		email = encryptionDecryptionUtil.decrypt(email);
		return new ResponseEntity<>(usersService.getUserPersonalDetails(email), HttpStatus.OK);
	}

	// method to get user donar Id by email
	@GetMapping("/getUserDonarId/{email}")
	public ResponseEntity<ApiResponse<String>> getUserDonarId(@PathVariable String email) {
		email = encryptionDecryptionUtil.decrypt(email);
		return new ResponseEntity<>(usersService.getUserDonarId(email), HttpStatus.OK);
	}

	// method to get existing user details by email
	@GetMapping("/getExistingUser/{email}")
	public ResponseEntity<ApiResponse<UsersDTO>> getExistingUserPersonalDetails(@PathVariable String email) {

		return new ResponseEntity<>(usersService.getExistingUserByEmail(email), HttpStatus.OK);
	}

	// method to get user personal details by donorId
	@GetMapping("/getUserDetailsByDonorId/{donorId}")
	public ResponseEntity<ApiResponse<UsersDTO>> getUserPersonalDetailsByDonorId(@PathVariable String donorId) {

		donorId = encryptionDecryptionUtil.decrypt(donorId);
		return new ResponseEntity<>(usersService.getUserPersonalDetailsByDonorId(donorId), HttpStatus.OK);
	}
  
	// forget password api
		@PostMapping("/forgetPassword/{donorId}")
		public ResponseEntity<?> forgetPassword(@PathVariable String donorId, HttpSession session)
				throws JsonProcessingException {
			return new ResponseEntity<>(usersService.forgetPassword(donorId, session), HttpStatus.OK);
		}

		//verify otp
		@PostMapping("/verifyForgotOtp")
		public ResponseEntity<ApiResponse<String>> verifyForgotOtp(@RequestParam String donarIdOrEmail, @RequestParam String otp){
			return new ResponseEntity<>(usersService.verifyForgotOtp(donarIdOrEmail,otp),
					HttpStatus.OK);
		}

		// set new password
		@PostMapping("/setUserNewPassword")
		public ResponseEntity<?> setUserNewPassword(@RequestBody LoginRequest formData, HttpSession session)
				throws JsonProcessingException {
			return new ResponseEntity<>(this.usersService.setUserNewPassword(formData, session), HttpStatus.OK);
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
	@PostMapping("/getAlluserWithWebId")
	public ResponseEntity<ApiResponse<List<UsersDTO>>> getAllusersWithWebId(
			@RequestBody DonorListRequestDTO requestDTO) {
		return new ResponseEntity<>(usersService.getAllUsersWithWebId(requestDTO), HttpStatus.OK);
	}

	@PostMapping("/approvedDonation")
	public ResponseEntity<?> approvedOnlineDonationOfUser(@RequestBody UsersDTO formData, HttpServletRequest request)
			throws JsonProcessingException, MessagingException {
//		ApiRequest apiRequest = new ApiRequest(formData);
		return new ResponseEntity<>(
				this.usersService.approvedOnlineDonationOfUser(formData, request),
				HttpStatus.OK);
	}

	@GetMapping("/getUserPersonalDetailsbyEmailOrDonorId")
	public ResponseEntity<ApiResponse<UsersDTO>> getUserPersonalDetailsbyEmailOrDonorId(
			@RequestParam String emailOrDonorId) {
		emailOrDonorId = encryptionDecryptionUtil.decrypt(emailOrDonorId);
		return new ResponseEntity<>(usersService.getUserPersonalDetailsbyEmailOrDonorId(emailOrDonorId), HttpStatus.OK);
	}

	@GetMapping("/getAllDonarId")
	public ResponseEntity<List<String>> getAllDonarIds() {
		List<String> donarId = usersService.getAllDonarId();
		return ResponseEntity.ok(donarId);
	}

	@GetMapping("/getAllUserId")
	public ResponseEntity<List<String>> getAllUserIds() {
		List<String> donarId = usersService.getAllUserIds();
		return ResponseEntity.ok(donarId);
	}

	@PostMapping("/sendOtp")
	public ResponseEntity<?> sendOtp(@RequestParam String email) {
		ApiResponse<?> result = new ApiResponse<>();
		try {
			otpService.sendOtpByEmail(email);
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Otp Send Successfully");
			result.setStatusCode(HttpStatus.OK.value());
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Invalid Mail", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<?> verifyOtp(@RequestParam String donarIdOrEmail, @RequestParam String otp) {
		ApiResponse<?> result = new ApiResponse<>();
		if (otp == null || donarIdOrEmail == null) {
			throw new CustomExceptionNodataFound("Please enter your Donar Id or Email  and OTP");
		}
		OtpModel otpModel = otpService.getOtpByEmail(donarIdOrEmail,otp);
		if (otpModel == null) {
			throw new CustomExceptionNodataFound("Your OTP has been expired... Please resend OTP...");
		}else{
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Otp is Valid");
			result.setStatusCode(HttpStatus.OK.value());
			otpModel.setOtpCode(null);
			otpModel.setOtpExpiryTime(null);
			otpRepository.save(otpModel);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/tooManyRequest")
	public ResponseEntity<ApiResponse> tooManyRequest() {
		ApiResponse response = new ApiResponse();
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.name());
		response.setMessage("Too many request, number request per minutes exceeds");
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
	}

	@PostMapping("/getUserDocuments")
	public ResponseEntity<ApiResponse<List<Document>>> getUserDocuments(@RequestBody PaginationRequestDTO<Integer> dto, HttpServletRequest request){
		if(isNull(dto.getData()) || dto.getData() == 0) {// if donor user is login
			String emailId = jwtHelper.getUsernameFromToken(request.getHeader("Authorization").substring(7));
			dto.setData(userRepository.findByEmailId(emailId).getUserId());
		}
		return new ResponseEntity<>(commonService.getUserDocuments(dto), HttpStatus.OK);
	}

	/**
	 * Rest endpoint to download plantation template
	 *
	 * @param response HttpServletResponse
	 */
	@PostMapping("/downloadUserDocument")
	public void downloadTemplate(@RequestBody Document document, HttpServletResponse response) {

		try {
			String path = document.getFilePath();
			FileInputStream inputStream = new FileInputStream(document.getFilePath());
			response.setContentType("application/octet-stream");

			// Set the filename based on the seasonType
			String fileName = document.getFileName();
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			IOUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception = " + e);
		}
	}// method

	@PostMapping("/exportDonationReport")
	public void exportExcelUserPlant(HttpServletResponse response, @RequestBody DonorListRequestDTO dto) {

		try {
			ByteArrayInputStream byteArrayInputStream = usersService.downloadDonationReport(dto);
			response.setContentType("application/octet-stream");

			// Set the filename based on the seasonType
			String fileName = "DonationReport.xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			IOUtils.copy(byteArrayInputStream, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception = " + e);
		}
	}// method
}
