package com.hariyali.serviceimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hariyali.dto.DonationDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hariyali.EnumConstants;
import com.hariyali.config.CustomUserDetailService;
import com.hariyali.config.JwtHelper;
import com.hariyali.dao.UserDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Address;
import com.hariyali.entity.Donation;
import com.hariyali.entity.PaymentInfo;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Recipient;
import com.hariyali.entity.Roles;
import com.hariyali.entity.UserPackages;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionDataAlreadyExists;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.AddressRepository;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.PaymentInfoRepository;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.repository.RecipientRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.ReceiptService;
import com.hariyali.service.UsersService;
import com.hariyali.utils.EmailService;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private CacheManager cacheManager;

	Random random = new Random(1000);

	private static final String OTP_CACHE_NAME = "otpCache";

	@Autowired
	ReceiptRepository receiptRepository;

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private UserDao userDao;

	@Autowired
	PaymentInfoRepository paymentIfoRepository;
	@Autowired
	private EmailService emailService;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private DonationRepository donationRepository;

	@Autowired
	private RecipientRepository recipientRepository;

	@Autowired
	private CustomUserDetailService customUserDetailService;

	@Autowired
	private UserPackageRepository userPackageRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private PaymentInfoRepository paymentInfoRepository;

	@Autowired
	private DonationServiceImpl donationServiceImpl;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	ReceiptService receiptService;

	private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

	public String generateDonorId() {
		String prefix = "100";
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int lastTwoDigits = currentYear % 100;
		String yearSuffix = String.format("%02d", lastTwoDigits);
		Random random = new Random();
		StringBuilder randomDigits1 = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			randomDigits1.append(random.nextInt(10)); // Append a random digit (0-9)
		}
		String randomDigits = randomDigits1.toString();

		return prefix + yearSuffix + randomDigits;
	}

	@Override
	public Users findByDonorId(String donorId) {
		return this.usersRepository.findByDonorId(donorId);
	}

	@Override
	public ApiResponse<UsersDTO> saveUserAndDonationsOffline(JsonNode jsonNode, HttpServletRequest request)
			throws JsonProcessingException, MessagingException {

		JsonNode userNode = jsonNode.get("user");
		ApiResponse<UsersDTO> response = null;
		JsonNode donationNode = userNode.get("donations");

		if (donationNode == null) {
			throw new CustomException("Donation not found");
		}

		String donationMode = null;
		String donationType = null;

		if (donationNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) donationNode;

			for (JsonNode donation : arrayNode) {
				if (donation.get("donationMode") != null)
					donationMode = donation.get("donationMode").asText();
				if (donation.get("donationType") != null)
					donationType = donation.get("donationType").asText();
			}
		}
		if (donationMode == null || donationType == null) {
			throw new CustomException("Donation mode OR  Donation Type not selected");
		}

		if (donationMode.equalsIgnoreCase("offline")) {

			// send email to user
			response = save(jsonNode, generateDonorId(), request);

			Users resulEntity = usersRepository.findByEmailId(userNode.get("emailId").asText());

			String subject = "Welcome To Hariyali";
			String content = "Dear Sir/Madam,\n \tWelcome to Project Hariyali."
					+ "The Mahindra Foundation,would like to thank you for your donation to Project Hariyali. The main objective of the project is to do 5 Billion Tree Plantation from 2026 in several parts of the Nation. "
					+ "The Tree Plantation is the main Agenda of the Project. "
					+ "The HARIYALI is a Partnership between Mahindra and Mahindra and the Nandi Foundation. The Project will be jointly managed by M&M and Nandi Foundation. \r\n"
					+ "Best wishes,\r\n" + "Below is your Donor Id : " + resulEntity.getDonorId() + "Team Hariyali\r\n"
					+ "\r\n";

			Receipt receipt = receiptRepository.getUserReceipt(resulEntity.getUserId());
			emailService.sendEmailWithAttachment(resulEntity.getEmailId(), subject, content, receipt.getReciept_Path());

			return response;
		} else {
			throw new CustomException("Invalid donation mode");
		}
	}

	// online donation

	@Override
	public ApiResponse<UsersDTO> saveUserAndDonationsOnline(JsonNode jsonNode, HttpServletRequest request)
			throws JsonProcessingException {

		JsonNode userNode = jsonNode.get("user");
		JsonNode donationNode = userNode.get("donations");

		if (donationNode == null) {
			throw new CustomException("Donation not found");
		}

		String donationMode = null;
		String donationType = null;

		if (donationNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) donationNode;

			for (JsonNode donation : arrayNode) {
				if (donation.get("donationMode") != null)
					donationMode = donation.get("donationMode").asText();
				if (donation.get("donationType") != null)
					donationType = donation.get("donationType").asText();
			}
		}
		if (donationMode == null || donationType == null) {
			throw new CustomException("Donation mode OR  Donation Type not selected");
		}

		else if (donationMode.equalsIgnoreCase("online")) {
			return save(jsonNode, null, null);
		}

		else {
			throw new CustomException("Invalid donation mode");
		}
	}

	public ApiResponse<UsersDTO> save(JsonNode jsonNode, String donarID, HttpServletRequest request)
			throws JsonProcessingException {

		ApiResponse<UsersDTO> response = null;

		JsonNode userNode = jsonNode.get("user");
		JsonNode donationNode = userNode.get("donations");

		String donationType = null;
		if (donationNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) donationNode;

			for (JsonNode donation : arrayNode) {
				if (donation.get("donationType") != null)
					donationType = donation.get("donationType").asText();
			}
		}

		// save user
		if (donationType.equalsIgnoreCase("Gift-Donate"))
			response = saveUser(userNode, null, request, false, null, null);
		else if (donationType.equalsIgnoreCase("Self-Donate"))
			response = saveUser(userNode, donarID, request, false, null, null);
		else
			throw new CustomExceptionNodataFound("No Donation Type is selected");

		// save donation
		ApiResponse<DonationDTO> apiResponse = donationServiceImpl.saveUserDonations(jsonNode, donarID, request);
		response.setGatewayURL(apiResponse.getGatewayURL());
		response.setEncRequest(apiResponse.getEncRequest());
		response.setAccessCode(apiResponse.getAccessCode());
		return response;

	}

	public ApiResponse<UsersDTO> saveUser(JsonNode userNode, String donarID, HttpServletRequest request,
			boolean isRecipient, String userEmailRecipent, JsonNode userNodeRecipient) {

		ApiResponse<UsersDTO> result = new ApiResponse<>();

		if (userNode == null) {
			throw new CustomException("No data found In User");
		}

		String token = null;
		String userName = null;
		Users userToken = null;
		if (request != null) {
			token = request.getHeader("Authorization");
			userName = jwtHelper.getUsernameFromToken(token.substring(7));
			userToken = this.usersRepository.findByEmailId(userName);
		}

		Gson gson = new GsonBuilder()
	            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
	            .create();

		Users user = gson.fromJson(userNode.toString(), Users.class);

		Users existingUser = usersRepository.findByEmailId(user.getEmailId());

		if (existingUser != null) {
			throw new CustomException("Email " + existingUser.getEmailId() + " already exists");
		}

		Date newDate = new Date();
		String createdBy = null;

		if (isRecipient) {
			String donationMode = userNodeRecipient.get("donations").get(0).get("donationMode").asText();
			// set created by based on donationMode when recipient dumps in user table
			if (donationMode.equalsIgnoreCase("online")) {
				createdBy = userEmailRecipent;
			} else {
				createdBy = userToken.getEmailId();
			}
		} else {
			// set created by based on donationMode
			String donationMode = userNode.get("donations").get(0).get("donationMode").asText();
			if (donationMode.equalsIgnoreCase("online")) {
				createdBy = userNode.get("emailId").asText();
			} else {
				createdBy = userToken.getEmailId();
			}
		}
		// set user password in encoded format
		user.setPassword(passwordEncoder.encode(EnumConstants.PASSWORD));

		// set created and updated date
		user.setCreatedDate(newDate);
		user.setModifiedDate(newDate);
		user.setDonorId(donarID);
		user.setCreatedBy(createdBy);
		user.setModifiedBy(createdBy);

		// set last login date
		user.setLastloginDate(newDate);

		// set attempts for incorrect username/password
		user.setAttempts(0);

		// set user role
		Roles role = new Roles();
		role.setUsertypeId(2);
		role.setUsertypeName("User");
		user.setUserRole(role);

		usersRepository.save(user);

		Users resulEntity = usersRepository.findByEmailId(user.getEmailId());

		// save user
		if (user.getAddress() != null) {
			for (Address address : user.getAddress()) {
				address.setCreatedDate(newDate);
				address.setModifiedDate(newDate);
				address.setCreatedBy(createdBy);
				address.setModifiedBy(createdBy);
				address.setUsers(resulEntity);
				addressRepository.save(address);
			}
		}

		result.setStatus(EnumConstants.SUCCESS);
		result.setStatusCode(HttpStatus.OK.value());
		result.setMessage("Data saved Successfully..");

		return result;
	}

	// get all users
	@Override
	public ApiResponse<Map<String, Object>> getUsers(int pageNo, int pageSize) {

		ApiResponse<Map<String, Object>> result = new ApiResponse<>();

		Pageable paging = PageRequest.of(pageNo - 1, pageSize);
		Page<Users> userMasters = this.userDao.getAllUsersByUserIdDesc(paging);

		Map<String, Object> response = new HashMap<>();
		response.put("users", userMasters.getContent());
		response.put("currentPage", userMasters.getNumber());
		response.put("totalItems", userMasters.getTotalElements());
		response.put("totalPages", userMasters.getTotalPages());

		if (userMasters.hasContent()) {
			result.setData(response);
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());
			result.setMessage("All Users Fetched Successfully..");
			return result;
		} else {
			result.setData(null);
			result.setStatus(EnumConstants.ERROR);
			result.setStatusCode(HttpStatus.NOT_FOUND.value());
			result.setMessage("No Data Found");
			return result;
		}
	}

	@Override
	public ApiResponse<Long> getDonorCunt() {
		ApiResponse<Long> result = new ApiResponse<>();
		long donorCount = this.userDao.getdonorCount();
		if (donorCount != 0) {
			result.setData(donorCount);
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());
			result.setMessage(" Data Fetched Successfully... ");

		}

		return result;
	}

	// delete user by user-code
	@Override
	public ApiResponse<UsersDTO> deleteUserById(int userId) throws CustomException {
		ApiResponse<UsersDTO> response = new ApiResponse<>();
		UsersDTO userResponse = this.userDao.deleteUserById(userId);

		if (userResponse != null) {

			response.setData(userResponse);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("User Has Been Deleted Successfully");

		}
		return response;

	}

	@Override
	public ApiResponse<UsersDTO> getUserByEmail(String email) {
		ApiResponse<UsersDTO> response = new ApiResponse<>();
		Object user = usersRepository.getUserByEmail(email);
		if (user == null)
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		//Gson gson = new Gson();
		Gson gson = new GsonBuilder()
	            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
	            .create();
		Users entity = gson.fromJson(user.toString(), Users.class);
		if (entity.getEmailId() != null) {
			if (entity.getDonorId() != null && entity.getWebId() == null) {
				throw new CustomExceptionDataAlreadyExists(
						"Donar with " + entity.getEmailId() + " is already Resigterd");
			}
			response.setData(modelMapper.map(entity, UsersDTO.class));
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("User found Successfully");
		} else
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		return response;
	}
	
	@Override
	public ApiResponse<UsersDTO> getExistingUserByEmail(String email) {
		ApiResponse<UsersDTO> response = new ApiResponse<>();
		Object user = usersRepository.getUserByEmail(email);
		if (user == null)
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		//Gson gson = new Gson();
		Gson gson = new GsonBuilder()
	            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
	            .create();
		Users entity = gson.fromJson(user.toString(), Users.class);
		if (entity.getEmailId() != null) {
//			if (entity.getDonorId() != null && entity.getWebId() == null) {
//				throw new CustomExceptionDataAlreadyExists(
//						"Donar with " + entity.getEmailId() + " is already Resigterd");
//			}
			response.setData(modelMapper.map(entity, UsersDTO.class));
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("User found Successfully");
		} else
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		return response;
	}

	@Override
	public ApiResponse<Object> getAllUsersWithDonarID() {
		ApiResponse<Object> response = new ApiResponse<>();

		Object result = usersRepository.getAllUsersWithDonarID();
		if (result != null) {
			response.setData(result);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;
		} else
			throw new CustomException("There is No user who has donarID");
	}

	@Override
	public ApiResponse<Object> getAllDonationOfSpecificUser(String email) {
		ApiResponse<Object> response = new ApiResponse<>();

		Optional<Users> userOptional = Optional.ofNullable(this.usersRepository.findByEmailId(email));

		if (userOptional.isPresent()) {
			Object result = usersRepository.getAllDonationOfSpecificUser(email);

			response.setData(result);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;

		} else {
			throw new CustomException("Given Email Doesn't Exist.");
		}
	}

	@Override
	public ApiResponse<UsersDTO> getUserPersonalDetails(String email) {
		ApiResponse<UsersDTO> response = new ApiResponse<>();
		Object user = usersRepository.getUserPersonalDetailsByEmail(email);
		if (user == null)
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		Gson gson = new Gson();
		Users entity = gson.fromJson(user.toString(), Users.class);
		if (entity.getEmailId() != null) {

			response.setData(modelMapper.map(entity, UsersDTO.class));
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("User found Successfully");
		}
		return response;

	}

	@Override
	public ApiResponse<UsersDTO> getUserPersonalDetailsByDonorId(String donorId) {
		ApiResponse<UsersDTO> response = new ApiResponse<>();
		Object user = usersRepository.getUserPersonalDetailsByDonorId(donorId);
		if (user == null)
			throw new CustomExceptionNodataFound("No user found with donor Id " + donorId);
		// Gson gson = new Gson();
		Gson gson = new GsonBuilder()
	            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
	            .create();
		Users entity = gson.fromJson(user.toString(), Users.class);
		if (entity.getEmailId() != null) {

			response.setData(modelMapper.map(entity, UsersDTO.class));
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("User found Successfully");
		}
		return response;

	}

	@Override
	public ApiResponse<UsersDTO> updateUser(JsonNode jsonNode, String emailId, HttpServletRequest request) {
		ApiResponse<UsersDTO> result = new ApiResponse<>();

		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));

		Users tokenUserUpdate = this.usersRepository.findByEmailId(userName);
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		JsonNode userNode = jsonNode.get("user");

		if (userNode == null) {
			throw new CustomException("No data found in User");
		}

		UsersDTO userDTO = gson.fromJson(userNode.toString(), UsersDTO.class);
		Users user = usersRepository.findByEmailId(emailId);

		if (user == null) {
			throw new CustomException("User not found");
		}

		Date newDate = new Date();

		// Update user fields
		user.setModifiedDate(newDate);
		user.setModifiedBy(tokenUserUpdate.getEmailId());
		user.setAttempts(0);

		// Update userDTO fields if provided
		if (userDTO.getFirstName() != null) {
			user.setFirstName(userDTO.getFirstName());
		}
		if (userDTO.getLastName() != null) {
			user.setLastName(userDTO.getLastName());
		}
		if (userDTO.getMobileNo() != null) {
			user.setMobileNo(userDTO.getMobileNo());
		}
		if (userDTO.getEmailId() != null) {
			user.setEmailId(userDTO.getEmailId());
		}
		if (userDTO.getOrganisation() != null) {
			user.setOrganisation(userDTO.getOrganisation());
		}
		if (userDTO.getPrefix() != null) {
			user.setPrefix(userDTO.getPrefix());
		}
		if (userDTO.getActivityType() != null) {
			user.setActivityType(userDTO.getActivityType());
		}
		if (userDTO.getDonarType() != null) {
			user.setDonarType(userDTO.getDonarType());
		}

		usersRepository.save(user);

		// Update addresses
		if (userDTO.getAddress() != null) {
			userDTO.getAddress().forEach(addressDTO -> {
				Address address = addressRepository.findByAddressId(addressDTO.getAddressId());
				if (address == null) {
					// Address not found, create a new one
					address = new Address();
					address.setUsers(user);
					address.setCreatedDate(newDate);
					address.setCreatedBy(tokenUserUpdate.getEmailId());
				}
				// Update address fields from addressDTO
				address.setStreet1(addressDTO.getStreet1());
				address.setStreet2(addressDTO.getStreet2());
				address.setStreet3(addressDTO.getStreet3());
				address.setCountry(addressDTO.getCountry());
				address.setState(addressDTO.getState());
				address.setCity(addressDTO.getCity());
				address.setPostalCode(addressDTO.getPostalCode());
				address.setModifiedDate(newDate);
				address.setModifiedBy(tokenUserUpdate.getEmailId());

				addressRepository.save(address);
			});
		}

		result.setStatus(EnumConstants.SUCCESS);
		result.setStatusCode(HttpStatus.OK.value());
		result.setMessage("Data Updated Successfully..");

		return result;
	}

	@Override
	public String getOtp(String donorId) {
		Users user = this.usersRepository.findByDonorId(donorId);
		if (user != null) {
			String email = user.getEmailId();
			System.err.println(email);
			Cache cache = cacheManager.getCache(OTP_CACHE_NAME);
			Cache.ValueWrapper valueWrapper = cache.get(email);
			if (valueWrapper == null) {
				return null;
			}
			return valueWrapper.get().toString();
		}
		return null;
	}

	@Override
	public void saveOtp(String donorId, String otp) {
		Users user = this.usersRepository.findByDonorId(donorId);
		if (user != null) {
			String email = user.getEmailId();
			System.err.println("305" + email);
			Cache cache = cacheManager.getCache(OTP_CACHE_NAME);
			cache.put(email, otp);
		}
	}

	@Override
	public ApiResponse<String> forgetPassword(String formData, HttpSession session) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		int otp = random.nextInt(999999);
		JsonNode jsonNode = objectMapper.readTree(formData);
		String donarID = jsonNode.get("donarID").asText();
		Users user = this.usersRepository.findByDonorId(donarID);
		emailService.sendSimpleEmail(user.getEmailId(), "forgetPassword",
				"Your OTP is " + otp + ". Use this OTP to activate your account: ");
		session.setAttribute("myotp", otp);
		session.setAttribute("donarID", donarID);

		long otpTimestamp = System.currentTimeMillis();
		session.setAttribute("otpTimestamp", otpTimestamp);
		String email = user.getEmailId();
		session.setAttribute("email", email);

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setData(null);
		response.setMessage("OTP Generated successfully and sent to email " + user.getEmailId());
		return response;
	}

	@Override
	public ApiResponse<String> verifyOtp(String formData, HttpSession session, HttpServletRequest request)
			throws JsonProcessingException {

		ApiResponse<String> response = new ApiResponse<>();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(formData);

		String enteredOTP = jsonNode.get("OTP").asText();
		Object storedOTPObject = session.getAttribute("myotp");

		if (storedOTPObject == null) {
			throw new CustomExceptionNodataFound("OTP not found");
		}

		String storedOTP = session.getAttribute("myotp").toString();
		if (storedOTP == null) {
			throw new CustomExceptionNodataFound("OTP not found");
		}
		long otpTimestamp = Long.parseLong(session.getAttribute("otpTimestamp").toString());
		long currentTime = System.currentTimeMillis();
		long timeLimitInMillis = 1 * 60 * 1000; // 1 minute

		if (enteredOTP.equals(storedOTP) && (currentTime - otpTimestamp <= timeLimitInMillis)) {
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setData(null);
			response.setMessage("OTP verified successfully");

			// Get the HttpSession object
			HttpSession sessionExpired = request.getSession(false); // Pass 'false' to avoid creating a new session

			// Invalidate the session
			if (sessionExpired != null) {
				sessionExpired.invalidate();
				System.err.println("session expired");
			}
		}

		else {
			if (!enteredOTP.equals(storedOTP)) {
				throw new CustomExceptionNodataFound("OTP not matched");
			}
			if (currentTime - otpTimestamp > timeLimitInMillis) {
				throw new CustomExceptionNodataFound("OTP validation time limit exceeded");
			} else {
				throw new CustomExceptionNodataFound("Entered OTP is incorrect");
			}
		}
		return response;
	}

	// forget User Password
	@Override
	public ApiResponse<String> forgetUserPassword(String formData, HttpSession session) throws JsonProcessingException {
		ApiResponse<String> res = new ApiResponse<>();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(formData);

		String userEmail = session.getAttribute("email").toString();

		String password = jsonNode.get("password").asText();

		Users user = this.usersRepository.findByEmailId(userEmail);

		if (user != null) {

			user.setPassword(this.passwordEncoder.encode(password));
			this.usersRepository.save(user);
			res.setStatus(EnumConstants.SUCCESS);
			res.setMessage("Password Has Been Updated Successfully..");
			res.setStatusCode(HttpStatus.OK.value());
			return res;
		}

		throw new CustomExceptionNodataFound("Unable to updated the password");
	}

	@Override
	public ApiResponse<String> activateAccount(String formData, HttpSession session) throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<Object> getAllUsersWithWebId() {
		ApiResponse<Object> response = new ApiResponse<>();

		Object result = usersRepository.getAllUsersWithWebId();
		if (result != null) {
			response.setData(result);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;
		} else
			throw new CustomException("There is No user who has webId");

	}

	@Override
	public ApiResponse<String> approvedOnlineDonationOfUser(String formData, HttpServletRequest request)
			throws JsonProcessingException, MessagingException {
		ApiResponse<String> result = new ApiResponse<>();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(formData);
		String webId = jsonNode.get("webId").asText();
		String status = jsonNode.get("status").asText();

		Users user = this.usersRepository.getUserByWebId(Integer.parseInt(webId));
		System.out.println("User:" + user.toString());
		System.out.println("username:" + userName);
		List<Donation> donation = this.donationRepository.getDonationDataByUserId(user.getUserId());
		Users recipientEmail = null;

		if (status.trim().equalsIgnoreCase("rejected")) {
			recipientEmail = handleDonationRejection(user, donation);
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Donation Rejected By " + userName);
			result.setStatusCode(HttpStatus.FORBIDDEN.value());
			sendRejectDonationEmails(user.getEmailId());
			sendRejectDonationEmails(recipientEmail.getEmailId());

		} else if (status.trim().equalsIgnoreCase("approved")) {
			recipientEmail = handleDonationApproval(user, donation, userName);
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Donation Approved By " + userName);
			result.setStatusCode(HttpStatus.OK.value());
//			sendDonationApprovalMail(user.getEmailId());
			String subject = "Welcome To Hariyali";
			String content = "Dear Sir/Madam,\n Welcome to Project Hariyali"
					+ "The Mahindra Foundation,would like to thank you for your donation to Project Hariyali. The main objective of the project is to do 5 Billion Tree Plantation from 2026 in several parts of the Nation. "
					+ "The Tree Plantation is the main Agenda of the Project. "
					+ "The HARIYALI is a Partnership between Mahindra and Mahindra and the Nandi Foundation. The Project will be jointly managed by M&M and Nandi Foundation. \r\n"
					+ "Best wishes,\r\n" + "Team Hariyali\r\n" + "\r\n";

			Receipt receipt = receiptRepository.getUserReceiptbyDonation(user.getUserId(),
					donation.get(0).getDonationId());
			emailService.sendEmailWithAttachment(user.getEmailId(), subject, content, receipt.getReciept_Path());
//			sendDonationApprovalMail(recipientEmail.getEmailId());
		} else {
			throw new CustomExceptionNodataFound("Status should Only be Approved or Rejected");
		}

		return result;
	}

	private void sendDonationApprovalMail(String emailId) {
		Users user = this.usersRepository.findByEmailId(emailId);
		if (user != null)

		{
			String subject = "Donation Approve";
			String content = "Dear User,\n\n" + "dear " + user.getEmailId() + "\n"
					+ "Donation made by you has been approved. \n" + "Your Donar Id is:" + user.getDonorId() + "\n"
					+ "Best regards,\n" + "Hariyali Team";

			emailService.sendSimpleEmail(user.getEmailId(), subject, content);
		}
	}

	private void sendResetPasswordEmails(String emailId) {
		// Construct the email subject and content

		Users email = this.usersRepository.findByEmailId(emailId);
		if (email != null)

		{
			UserDetails userDetails = this.customUserDetailService.loadUserByUsername(email.getEmailId());
			String token = this.jwtHelper.generateToken(userDetails);

			String subject = "Reset Password";
			String content = "Dear User,\n\n"
					+ "You have requested to reset your password. Please click on the link below to proceed:\n"
					+ EnumConstants.resetPasswordLink + token
					+ "If you didn't request a password reset, please ignore this email.\n\n" + "Best regards,\n"
					+ "Hariyai Team";

			emailService.sendSimpleEmail(email.getEmailId(), subject, content);
		}
	}

	private void sendRejectDonationEmails(String emailId) {
		// Construct the email subject and content

		Users user = this.usersRepository.findByEmailIdForDeletedUser(emailId);
		if (user != null)

		{
			String subject = "Reject Donation";
			String content = "Dear User,\n\n" + "dear " + user.getEmailId() + "\n"
					+ "Donation made by you has rejected \n" + "Best regards,\n" + "Hariyali Team";

			emailService.sendSimpleEmail(user.getEmailId(), subject, content);
		}
	}

	// donation approved
//	private Users handleDonationApproval(Users user, List<Donation> donation, String userName) {
//		Users recipientEmail = null;
//
//		if (donation != null) {
//			for (Donation d : donation) {
//				int donationId = d.getDonationId();
//				List<Recipient> recipients = this.recipientRepository.getRecipientDataByDonationId(donationId);
//
//				for (Recipient recipient : recipients) {
//					recipientEmail = this.usersRepository.findByEmailId(recipient.getEmailId());
//					recipientEmail.setDonorId(generateDonorId());
//				}
//			}
//		}
//
//		user.setIsApproved(true);
//		user.setIsDeleted(false);
//		recipientEmail.setIsDeleted(false);
//		user.setDonorId(recipientEmail.getDonorId());
//
//		user.setCreatedBy(userName);
//		user.setModifiedBy(userName);
//		user.setModifiedDate(new Date());
//		System.out.println("new User:" + user.toString());
//		this.usersRepository.save(user);
//
//		recipientEmail.setCreatedBy(userName);
//		recipientEmail.setModifiedBy(userName);
//		recipientEmail.setModifiedDate(new Date());
//		this.usersRepository.save(recipientEmail);
//		System.out.println("new User:" + recipientEmail.toString());
//
//		return recipientEmail;
//	}

	private Users handleDonationApproval(Users user, List<Donation> donations, String userName) {
		Users recipientEmail = null;

		if (donations != null) {
			for (Donation d : donations) {
				if (d.getDonationType().equalsIgnoreCase("gift-Donate")) {
					List<Recipient> recipients = this.recipientRepository
							.getRecipientDataByDonationId(d.getDonationId());
					for (Recipient recipient : recipients) {
						recipientEmail = this.usersRepository.findByEmailId(recipient.getEmailId());
						System.err.println("Recipient" + recipientEmail.toString());
						recipientEmail.setDonorId(generateDonorId());
						recipientEmail.setIsApproved(true);
						recipientEmail.setIsDeleted(false);
						recipientEmail.setIsDeleted(false);
						recipientEmail.setCreatedBy(userName);
						recipientEmail.setModifiedBy(userName);
						recipientEmail.setModifiedDate(new Date());
						usersRepository.save(recipientEmail);
						System.err.println("After save Recipient" + recipientEmail.toString());
						System.out.println("new User:" + recipientEmail);

					}

				} else if (d.getDonationType().equalsIgnoreCase("self-Donate")) {
					List<Users> users = this.usersRepository.getUserDataByDonationId(d.getDonationId());
					for (Users userdata : users) {
						recipientEmail = this.usersRepository.findByEmailId(userdata.getEmailId());
						recipientEmail.setDonorId(generateDonorId());
						recipientEmail.setIsApproved(true);
						recipientEmail.setIsDeleted(false);
						recipientEmail.setIsDeleted(false);
						recipientEmail.setCreatedBy(userName);
						recipientEmail.setModifiedBy(userName);
						recipientEmail.setModifiedDate(new Date());
						usersRepository.save(recipientEmail);
						System.out.println("new User:" + recipientEmail);
					}
				} else {
					throw new CustomExceptionNodataFound("Please select Dontation Type");
				}
				try {
					String paymentStatus = d.getPaymentInfo().get(0).getPaymentStatus();
					if (paymentStatus.equalsIgnoreCase("Completed")) {
						receiptService.generateReceipt(d);
						emailService.sendGiftingLetterEmail(recipientEmail.getEmailId(), user);
					} else {
						throw new CustomException("Your payment status is" + paymentStatus);
					}
				} catch (Exception e) {
					throw new CustomException("Payment not perform.");
				}
			}
		}

		return recipientEmail;

	}

	// reject donation
	private Users handleDonationRejection(Users user, List<Donation> donation) {
		user.setIsDeleted(true);
		user.setDonorId(null);
		this.usersRepository.save(user);

		if (donation != null) {
			for (Donation d : donation) {
				deleteDonation(user, d);
			}
		}
		return user;
	}

	private void deleteDonation(Users user, Donation donation) {
		donation.setIsDeleted(true);
		this.donationRepository.save(donation);

		int donationId = donation.getDonationId();
		deletePaymentInfoByDonationId(donationId);
		deleteUserPackagesByDonationId(donationId);
		deleteRecipientsByDonationId(user, donationId);
	}

	// delete all payments
	private void deletePaymentInfoByDonationId(int donationId) {
		List<PaymentInfo> payments = this.paymentInfoRepository.findPaymentByDonationId(donationId);
		if (payments != null) {
			for (PaymentInfo payment : payments) {
				payment.setIsDeleted(true);
				this.paymentInfoRepository.save(payment);
			}
		}
	}

	// delete all user packages
	private void deleteUserPackagesByDonationId(int donationId) {
		List<UserPackages> packages = this.userPackageRepository.findPackageByDonationId(donationId);
		for (UserPackages userPackage : packages) {
			userPackage.setIsdeleted(true);
			this.userPackageRepository.save(userPackage);
		}
	}

	// delete all respective recipients of user
	private void deleteRecipientsByDonationId(Users user, int donationId) {
		List<Recipient> recipients = this.recipientRepository.getRecipientDataByDonationId(donationId);
		for (Recipient recipient : recipients) {
			Users recipientEmail = this.usersRepository.findByEmailId(recipient.getEmailId());
			recipientEmail.setIsDeleted(true);
			this.usersRepository.save(recipientEmail);

			recipient.setIsDeleted(true);
			this.recipientRepository.save(recipient);

			int recipientId = recipient.getRecipientId();
			deleteRecipientAddress(recipientId, recipientEmail.getEmailId());
			deleteAddressByUserId(recipientEmail.getUserId());
			deleteAddressByUserId(user.getUserId());
		}
	}

//delete recipientAddress 
	private void deleteRecipientAddress(int recipientId, String emailId) {

		Address recipientAddress = this.addressRepository.findAddressByRecipientId(recipientId);
		if (recipientAddress.getRecipient().getEmailId().equals(emailId)) {
			recipientAddress.setIsDeleted(true);
			this.addressRepository.save(recipientAddress);
		}
	}

	// delete address of user
	private void deleteAddressByUserId(int userId) {
		Address address = this.addressRepository.findAddressByUserId(userId);
		address.setIsDeleted(true);
		this.addressRepository.save(address);
	}

	@Override
	public ApiResponse<UsersDTO> getUserPersonalDetailsbyEmailOrDonorId(String emailOrDonorId) {
		ApiResponse<UsersDTO> response = new ApiResponse<>();

		try {
			Object user = usersRepository.getUserPersonalDetailsByDonorId(emailOrDonorId);

			if (user == null) {
				user = usersRepository.getUserPersonalDetailsByEmail(emailOrDonorId);
			}

			if (user != null) {
				Gson gson = new Gson();
				Users entity = gson.fromJson(user.toString(), Users.class);

				if (entity.getEmailId() != null || entity.getDonorId() != null) {
					response.setData(modelMapper.map(entity, UsersDTO.class));
					response.setStatus(EnumConstants.SUCCESS);
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("User found Successfully");
				} else {
					response.setStatus(EnumConstants.ERROR);
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setMessage("User not found");
				}
			} else {
				response.setStatus(EnumConstants.ERROR);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("User not found");
			}
		} catch (Exception e) {
			response.setStatus(EnumConstants.ERROR);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("An error occurred while fetching user details.");
		}
		return response;
	}

	// get All donarId
	@Override
	public List<String> getAllDonarId() {
		return usersRepository.getAllDonorId();
	}

}
