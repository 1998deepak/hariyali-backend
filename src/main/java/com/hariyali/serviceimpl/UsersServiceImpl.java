package com.hariyali.serviceimpl;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hariyali.EnumConstants;
import com.hariyali.config.CustomUserDetailService;
import com.hariyali.config.JwtHelper;
import com.hariyali.dao.UserDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.dto.DonorListRequestDTO;
import com.hariyali.dto.LoginRequest;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Address;
import com.hariyali.entity.Document;
import com.hariyali.entity.Donation;
import com.hariyali.entity.OtpModel;
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
import com.hariyali.repository.DocumentRepository;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.OtpRepository;
import com.hariyali.repository.PaymentInfoRepository;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.repository.RecipientRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.ReceiptService;
import com.hariyali.service.UsersService;
import com.hariyali.utils.AES;
import com.hariyali.utils.CommonService;
import com.hariyali.utils.EmailService;
import com.hariyali.utils.EncryptionDecryptionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	OtpServiceImpl otpServiceImpl;

	@Autowired
	ReceiptService receiptService;

	@Autowired
	CommonService commonService;

	@Autowired
	JwtServiceImpl jwtService;
	
	@Autowired
	OtpRepository otpRepository;

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	private EncryptionDecryptionUtil encryptionDecryptionUtil;

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

	public String generateWebId() {
		String prefix = "WEBID_100";
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
	@Transactional
	public ApiResponse<UsersDTO> saveUserAndDonationsOffline(UsersDTO usersDTO, HttpServletRequest request)
			throws JsonProcessingException, MessagingException {

		validateDonation(usersDTO, "offline");

		// send email to user
		ApiResponse<UsersDTO> response = save(usersDTO, commonService.createDonarIDORDonationID("user"), request);

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		Receipt receipt = receiptRepository.getUserReceipt(resulEntity.getUserId());
		// emailService.sendEmailWithAttachment(resulEntity.getEmailId(),
		// EnumConstants.subject, EnumConstants.content,
		// receipt.getReciept_Path(), resulEntity);
//		emailService.sendEmailWithAttachment(resulEntity.getEmailId(), EnumConstants.subject, EnumConstants.content,
//				receipt.getReciept_Path(), resulEntity);

		return response;

	}

	// online donation

	@Override
	@Transactional
	public ApiResponse<UsersDTO> saveUserAndDonationsOnline(UsersDTO usersDTO, HttpServletRequest request)
			throws JsonProcessingException {

		validateDonation(usersDTO, "online");
		return save(usersDTO, null, null);
//		return null;
	}

	private void validateDonation(UsersDTO usersDTO, String donationMode) {
		ofNullable(usersDTO.getDonations()).filter(data -> !data.isEmpty())
				.orElseThrow(() -> new CustomException("Donation not found"));

		DonationDTO donationDTO = of(usersDTO.getDonations()).get().stream().findFirst().get();
		of(donationDTO).map(DonationDTO::getDonationMode).filter(mode -> !mode.isEmpty())
				.orElseThrow(() -> new CustomException("Donation mode OR  Donation Type not selected"));
		of(donationDTO).map(DonationDTO::getDonationType).filter(mode -> !mode.isEmpty())
				.orElseThrow(() -> new CustomException("Donation mode OR  Donation Type not selected"));

		of(donationDTO).map(DonationDTO::getDonationMode).filter(mode -> donationMode.equalsIgnoreCase(mode))
				.orElseThrow(() -> new CustomException("Invalid donation mode"));
		if (usersDTO.getMeconnectId() != null) {
			if (!usersDTO.getMeconnectId().isEmpty()) {
				try {
					String str = AES.decrypt(usersDTO.getMeconnectId());
					String[] parts = str.split("\\|\\|");
					System.out.println(parts[0] + ":=>" + parts[1]);
				} catch (Exception e) {
					throw new CustomException("Something went wrong...!");
				}
			}
		}

	}

	public ApiResponse<UsersDTO> save(UsersDTO usersDTO, String donarID, HttpServletRequest request)
			throws JsonProcessingException {

		ApiResponse<UsersDTO> response = null;
		DonationDTO donationDTO = of(usersDTO.getDonations()).get().stream().findFirst().get();
		// save user
		if ("Gift-Donate".equalsIgnoreCase(donationDTO.getDonationType()))
			response = saveUser(usersDTO, null, request, false, null, donationDTO.getDonationMode());
		else if ("Self-Donate".equalsIgnoreCase(donationDTO.getDonationType()))
			response = saveUser(usersDTO, donarID, request, false, null, donationDTO.getDonationMode());
		else
			throw new CustomExceptionNodataFound("No Donation Type is selected");

		// save donation
		ApiResponse<DonationDTO> apiResponse = donationServiceImpl.saveUserDonations(usersDTO, donarID, request);
		response.setGatewayURL(apiResponse.getGatewayURL());
		response.setEncRequest(apiResponse.getEncRequest());
		response.setStatus(apiResponse.getStatus());
		response.setAccessCode(apiResponse.getAccessCode());
		if (("OTHERTHANINDIA").equalsIgnoreCase(apiResponse.getStatus())) {
			UsersDTO usersDTO2 = new UsersDTO();
			usersDTO2.setDonations(Arrays.asList(apiResponse.getData()));
			response.setData(usersDTO2);
		}

		return response;

	}

	public ApiResponse<UsersDTO> saveUser(UsersDTO usersDTO, String donarID, HttpServletRequest request,
			boolean isRecipient, String userEmailRecipent, String donationMode) {

		ApiResponse<UsersDTO> result = new ApiResponse<>();

		if (isNull(usersDTO)) {
			throw new CustomException("No data found In User");
		}

		Users user = modelMapper.map(usersDTO, Users.class);
		Users existingUser = usersRepository.findByEmailId(user.getEmailId());

		if (existingUser != null) {
			if(existingUser.getWebId() != null) {
				throw new CustomException("Email " + existingUser.getEmailId() + " already exists");
			} else{
				user.setUserId(existingUser.getUserId());
			}
		}

		Date newDate = new Date();
		String createdBy = null;

		if ("online".equalsIgnoreCase(donationMode)) {
			createdBy = isRecipient ? userEmailRecipent : usersDTO.getEmailId();
		} else {
			Users userToken = null;
			if (request != null) {
				String token = request.getHeader("Authorization");
				String userName = jwtHelper.getUsernameFromToken(token.substring(7));
				userToken = this.usersRepository.findByEmailId(userName);
			}
			createdBy = ofNullable(userToken).map(Users::getEmailId).orElse("");
		}

		// set user password in encoded format
//		user.setPassword(passwordEncoder.encode(EnumConstants.PASSWORD));

		// set created and updated date
		user.setCreatedDate(newDate);
		user.setModifiedDate(newDate);
		user.setDonorId(donarID);
		user.setCreatedBy(createdBy);
		user.setModifiedBy(createdBy);
		user.setCitizenship(usersDTO.getCitizenship());
		user.setCountry(usersDTO.getCountry());
		user.setOrganisation(usersDTO.getOrganisation());

		// set last login date
		user.setLastloginDate(newDate);

		// set attempts for incorrect username/password
		user.setAttempts(0);

		// set user role
		Roles role = new Roles();
		role.setUsertypeId(2);
		role.setUsertypeName("User");
		user.setUserRole(role);
		if ("online".equalsIgnoreCase(donationMode)) {
			user.setApprovalStatus("Pending");
		} else {
			user.setApprovalStatus("Approved");
		}
		user.setIsDeleted(false);
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
		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY).create();
		Users entity = gson.fromJson(user.toString(), Users.class);
		if (entity.getEmailId() != null) {
			if (entity.getDonorId() != null) {
				throw new CustomExceptionDataAlreadyExists("Donor with " + entity.getEmailId()
						+ " is already registered, Kindly do click here to login and continue your donation!");
			}
			response.setData(modelMapper.map(user, UsersDTO.class));
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
		} else
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		return response;
	}

	public ApiResponse<UsersDTO> getExistingUserByEmail(String email) {
		ApiResponse<UsersDTO> response = new ApiResponse<>();
		Object user = usersRepository.getUserByEmail(email);
		if (user == null)
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY).create();
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

		Optional<Users> userOptional = ofNullable(this.usersRepository.findByEmailId(email));

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
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY).create();
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
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY).create();
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
	public ApiResponse<UsersDTO> updateUser(UsersDTO userDTO, String emailId, HttpServletRequest request) {
		ApiResponse<UsersDTO> result = new ApiResponse<>();

		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));

		Users tokenUserUpdate = this.usersRepository.findByEmailId(userName);

		ofNullable(userDTO).orElseThrow(() -> new CustomException("No data found in User"));

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
	public ApiResponse<String> forgetPassword(String donorId, HttpSession session) throws JsonProcessingException {
		Random random = new Random();
		int otpValue = random.nextInt((int) Math.pow(10, 6));
		Users user = jwtService.findUserByDonorIdOrEmailId(donorId);
		if (user != null) {
			String otp = String.format("%0" + 6 + "d", otpValue);
			OtpModel otpModel = new OtpModel();
			otpModel.setOtpCode(otp);
			otpModel.setDonarIdOrEmail(user.getEmailId());
			otpModel.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
			otpModel.setUsers(user);
			otpRepository.save(otpModel);
			String body = "Dear Donor,<br><br>" + "<br>Please use OTP to set new password - " + otp
					+ "<br><br>-Team Hariyali<br><br>"
					+ "PS: For any support or queries please reach out to us at <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a>";
			emailService.sendSimpleEmail(user.getEmailId(), "Project Hariyali - Forgot Password", body);
			

		} else {
			throw new CustomException("User not forund");
		}
		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setData(null);
		response.setMessage("OTP Generated successfully and sent to email " + user.getEmailId());
		return response;
	}

	// verify otp
	@Override
	public ApiResponse<String> verifyForgotOtp(String email, String otp) {
		ApiResponse<String> result = new ApiResponse<>();
		Users user = jwtService.findUserByDonorIdOrEmailId(email);
		 OtpModel otpModel = otpServiceImpl.getOtpByEmail(email); 
//		 OtpModel otpModel = otpServiceImpl.findByOtp(otp);
		if (user == null || !otp.equals(otpModel.getOtpCode())) {
			throw new CustomExceptionNodataFound("Invalid OTP");
		} else {
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("OTP verified successfully");
			result.setStatusCode(HttpStatus.OK.value());

			return result;
		}
	}

	// set new user Password
	@Override
	public ApiResponse<String> setUserNewPassword(LoginRequest request, HttpSession session)
			throws JsonProcessingException {
		ApiResponse<String> res = new ApiResponse<>();

		String password = request.getPassword();

		Users user = jwtService.findUserByDonorIdOrEmailId(request.getEmail());

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
	public ApiResponse<List<UsersDTO>> getAllUsersWithWebId(DonorListRequestDTO requestDTO) {
		ApiResponse<List<UsersDTO>> response = new ApiResponse<>();
		Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize());

		Page<Object[]> result = usersRepository.getAllUsersWithWebId(ofNullable(requestDTO.getSearchText()).orElse(""),
				requestDTO.getStatus(), StringUtils.trimToNull(requestDTO.getDonorType()), pageable);

		if (!isNull(result) && !result.getContent().isEmpty()) {
			List<UsersDTO> usersDTOS = of(result.getContent()).get().stream().map(this::toUsersDTO)
					.collect(Collectors.toList());
			response.setData(usersDTOS);
			response.setTotalPages(result.getTotalPages());
			response.setTotalRecords(result.getTotalElements());
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;
		} else
			throw new CustomException("There is No user who has webId");

	}

	private UsersDTO toUsersDTO(Object[] user) {
		UsersDTO dto = new UsersDTO();
		if (user.length > 0) {
			dto.setUserId(ofNullable(user[0]).map(String::valueOf).map(Integer::parseInt).orElse(0));
			dto.setWebId(ofNullable(user[1]).map(String::valueOf).orElse(""));
			dto.setDonorId(ofNullable(user[2]).map(String::valueOf).orElse(""));
			dto.setFirstName(ofNullable(user[3]).map(String::valueOf).orElse(""));
			dto.setLastName(ofNullable(user[4]).map(String::valueOf).orElse(""));
			dto.setDonarType(ofNullable(user[5]).map(String::valueOf).orElse(""));
			dto.setOrganisation(ofNullable(user[6]).map(String::valueOf).orElse(""));
			dto.setApprovalStatus(ofNullable(user[7]).map(String::valueOf).orElse(""));
			dto.setEmailId(ofNullable(user[8]).map(String::valueOf).orElse(""));
			dto.setRemark(ofNullable(user[9]).map(String::valueOf).orElse(""));

		}
		return dto;
	}

	@Override
	@Transactional
	public ApiResponse<String> approvedOnlineDonationOfUser(UsersDTO usersDTO, HttpServletRequest request)
			throws JsonProcessingException, MessagingException {
		ApiResponse<String> result = new ApiResponse<>();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));

		String webId = usersDTO.getWebId();
		String status = usersDTO.getStatus();

		Users user = this.usersRepository.getUserByWebId(webId);
		user.setRemark(usersDTO.getRemark());
		user.setApprovalStatus(usersDTO.getApprovalStatus());

		List<Donation> donation = user.getDonations();// this.donationRepository.getDonationDataByUserId(user.getUserId());
		Users recipientEmail = null;

		if ("Rejected".equalsIgnoreCase(usersDTO.getApprovalStatus())) {
			recipientEmail = handleDonationRejection(user, Collections.singletonList(donation.get(0)));
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Donation Rejected By " + userName);
			result.setStatusCode(HttpStatus.FORBIDDEN.value());
			sendRejectDonationEmails(user);
		} else if ("Approved".equalsIgnoreCase(usersDTO.getApprovalStatus())) {
			recipientEmail = handleDonationApproval(user, Collections.singletonList(donation.get(0)), userName);
			result.setStatus(EnumConstants.SUCCESS);
			result.setMessage("Donation Approved By " + userName);
			result.setStatusCode(HttpStatus.OK.value());
//			Receipt receipt = receiptRepository.getUserReceiptbyDonation(user.getUserId(),
//					donation.get(0).getDonationId());
//			emailService.sendEmailWithAttachment(user.getEmailId(), EnumConstants.subject, EnumConstants.content,
//					receipt.getReciept_Path(), recipientEmail);
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

	private void sendRejectDonationEmails(Users user) {
		// Construct the email subject and content

		if (user != null)

		{
			String subject = "Project Hariyali: Donation Failure";
			String content = "Dear %s,<br>"
					+ "Thank you for your interest in Project Hariyali. Unfortunately we are unable to process your transaction. Please reach out to us at"
					+ "<a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> for any queries"
					+ "Thank You<br>" + "Team Hariyali<br>" + "Mahindra Foundation<br>" + "3rd Floor, Cecil Court,<br>"
					+ "Near Regal Cinema,<br>" + "Mahakavi Bhushan Marg,<br>" + "Mumbai 400001<br>"
					+ "<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
					+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
			String mailBody = String.format(content, user.getFirstName(), content);
			emailService.sendSimpleEmail(user.getEmailId(), subject, mailBody);
		}
	}

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
						recipientEmail.setDonorId(recipientEmail.getDonorId());
						recipientEmail.setIsDeleted(false);
						recipientEmail.setCreatedBy(userName);
						recipientEmail.setModifiedBy(userName);
						recipientEmail.setModifiedDate(new Date());
						usersRepository.save(recipientEmail);
						System.err.println("After save Recipient" + recipientEmail.toString());
						System.out.println("new User:" + recipientEmail);
					}
					user.setIsApproved(true);
					user.setModifiedDate(new Date());
					usersRepository.save(user);

				} else if (d.getDonationType().equalsIgnoreCase("self-Donate")) {
					List<Users> users = this.usersRepository.getUserDataByDonationId(d.getDonationId());
					for (Users userdata : users) {
						recipientEmail = this.usersRepository.findByEmailId(userdata.getEmailId());
						recipientEmail.setDonorId(recipientEmail.getDonorId());
						recipientEmail.setIsApproved(true);
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

					// added bug fix because payment info was getting null
					String paymentStatus = paymentIfoRepository.getPaymentStatusByDonationId(d.getDonationId());
//					String paymentStatus = d.getPaymentInfo().get(0).getPaymentStatus();

					if ("Completed".equalsIgnoreCase(paymentStatus) || "Success".equalsIgnoreCase(paymentStatus)) {
						receiptService.generateReceipt(d);
						Receipt receipt = receiptRepository.getUserReceipt(user.getUserId());
						Users recipientData = usersRepository.findByEmailId(recipientEmail.getEmailId());
						emailService.sendReceiptWithAttachment(user, d.getOrderId(), receipt);
						emailService.sendThankyouLatter(user.getEmailId(), user);

					} else {
						sendRejectDonationEmails(user);
					}
				} catch (Exception e) {
					log.error("Exception = {}", e);
					throw new CustomException("Payment not perform.");
				}
				d.setApprovalDate(new Date());
				d.setIsApproved(true);
				d.setApprovalStatus("Approved");
			}
			donationRepository.saveAll(donations);
		}

		return recipientEmail;

	}

	// reject donation
	private Users handleDonationRejection(Users user, List<Donation> donations) {
		user.setIsDeleted(true);
		user.setDonorId(null);
		this.usersRepository.save(user);

		if (donations != null) {
			for (Donation donation : donations) {
				donation.setIsDeleted(true);
				if (donation.getPaymentInfo() != null) {
					for (PaymentInfo payment : donation.getPaymentInfo()) {
						payment.setIsDeleted(true);
					}
					this.paymentInfoRepository.saveAll(donation.getPaymentInfo());
				}
				if (donation.getUserPackage() != null) {
					for (UserPackages packages : donation.getUserPackage()) {
						packages.setIsdeleted(true);
					}
					this.userPackageRepository.saveAll(donation.getUserPackage());
				}
				if (donation.getRecipient() != null) {
					for (Recipient recipient : donation.getRecipient()) {
						recipient.setIsDeleted(true);
						Users recipientEmail = this.usersRepository.findByEmailId(recipient.getEmailId());
						recipientEmail.setIsDeleted(true);
						this.usersRepository.save(recipientEmail);
					}

					this.recipientRepository.saveAll(donation.getRecipient());
				}

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
				// Gson gson = new Gson();
				Gson gson = new GsonBuilder().registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY).create();
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

	@Override
	public List<String> getAllUserIds() {
		List<String> userIds = usersRepository.getAllDonorId();
		userIds.addAll(usersRepository.getAllEmailId());
		return userIds;
	}

	@Override
	public ApiResponse<List<Donation>> getUserDonations(String email, Integer pageNo, Integer pageSize) {
		ApiResponse<List<Donation>> response = new ApiResponse<>();

		Page<Donation> result = donationRepository.getUserDonations(email, PageRequest.of(pageNo, pageSize));
		if(result.getTotalElements() == 0){
			throw new CustomException("No user donatation found");
		} else{
			response.setTotalRecords(result.getTotalElements());
			response.setData(result.getContent());
			response.setStatus("Success");
		}
		return response;
	}

	@Override
	public ApiResponse<String> getUserDonarId(String email) {
		ApiResponse<String> response = new ApiResponse<>();
		String donarId = usersRepository.findDonarIdByEmail(email);
		if (donarId == null)
			throw new CustomExceptionNodataFound("No user found with emailId " + email);
		if (donarId != null) {
			response.setData(donarId);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Donar Id found Successfully");
		}
		return response;

	}

	@Override
	@Transactional
	public ApiResponse<String> changePassword(LoginRequest request, String token) {
		ApiResponse<String> response = new ApiResponse<>();
		String password = encryptionDecryptionUtil.decrypt(request.getPassword());
		String userName = jwtHelper.getUsernameFromToken(token);

		Users user = usersRepository.findByEmailId(userName);
		if (passwordEncoder.matches(password, user.getPassword())) {
			throw new CustomException("New password is same as old password!");
		}

		user.setPassword(passwordEncoder.encode(password));
		usersRepository.save(user);
		response.setStatus("Success");
		response.setMessage("User password changed successfully!");
		return response;
	}// method

}
