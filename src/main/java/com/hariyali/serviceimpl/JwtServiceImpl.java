package com.hariyali.serviceimpl;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.EnumConstants;
import com.hariyali.config.CustomUserDetailService;
import com.hariyali.config.JwtHelper;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.LoginRequest;
import com.hariyali.entity.OtpModel;
import com.hariyali.entity.TokenLoginUser;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.JwtService;
import com.hariyali.service.TokenLoginUserService;
import com.hariyali.utils.EmailService;

@Service
public class JwtServiceImpl implements JwtService {

	String startDate;

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	OtpServiceImpl otpService;

	@Autowired
	private CustomUserDetailService customUserDetailService;

	@Autowired
	private TokenLoginUserService tokenLoginUserService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

	@Value("${user.account.locktime}")
	private Integer lockTime;

	@Override
	public ApiResponse<String> login(LoginRequest request) {

		ApiResponse<String> result = new ApiResponse<>();

		if (request.getUsername() != null || request.getPassword() != null) {

			Optional<Users> userResponse = Optional
					.ofNullable(this.usersRepository.findByDonorId(request.getUsername()));

			Users user = null;
			if (userResponse.isPresent()) {
				user = userResponse.get();

				System.err.println("user" + user);
				logger.debug("User found - {}", user.getDonorId());
				if (user.getLastloginDate() != null) {
					Date dt = user.getLastloginDate();
					long updatedDate = dt.getTime();
					long currentDate = new Date().getTime();
					long timeDiff = currentDate - updatedDate;
					timeDiff = timeDiff / 1000; // time difference in sec
					long lockTimeSec = lockTime * 60; // lockTime in sec

					// is user locked or not
					boolean isAccountUnlock = (lockTimeSec - timeDiff) > 0 ? false : true;
					if (isAccountUnlock && user.getAttempts() >= 3) {
						user.setAttempts(0);
						user.setLastloginDate(new Date());
						userRepository.save(user);
					}
				}
				if (request.getUsername().equals(user.getDonorId())
						&& this.passwordEncoder.matches(request.getPassword(), user.getPassword())
						&& (user.getAttempts() < 3)) {
					// Generate token
					UserDetails userDetails = this.customUserDetailService.loadUserByUsername(request.getUsername());
					String token = this.jwtHelper.generateToken(userDetails);
					TokenLoginUser loginUser1 = tokenLoginUserService.findByUsernameDonorId(request.getUsername());
					TokenLoginUser loginUserEmailIdLoginUser = tokenLoginUserService
							.findByUsernameEmailId(user.getEmailId());

					if (loginUser1 == null && loginUserEmailIdLoginUser == null) {
						loginUser1 = new TokenLoginUser();
						loginUser1.setToken(token);
						loginUser1.setDonorId(request.getUsername());
						loginUser1.setFlag(true);
						loginUser1.setLastUpdatedOn(new Date());
						tokenLoginUserService.saveUser(loginUser1);

					} else if (loginUserEmailIdLoginUser != null && loginUserEmailIdLoginUser.isFlag()) {
						loginUserEmailIdLoginUser.setFlag(true);
						loginUserEmailIdLoginUser.setDonorId(request.getUsername());
						loginUserEmailIdLoginUser.setLastUpdatedOn(new Date());
						loginUserEmailIdLoginUser.setToken(token);
						tokenLoginUserService.updateToken(loginUserEmailIdLoginUser);
					}

					else if (loginUser1 != null && !loginUser1.isFlag()) {
						loginUser1.setFlag(true);
						loginUser1.setLastUpdatedOn(new Date());
						loginUser1.setToken(token);
						tokenLoginUserService.updateToken(loginUser1);
					} else if (loginUser1 != null && loginUser1.isFlag()) {

						loginUser1.setLastUpdatedOn(new Date());
						loginUser1.setToken(token);
						tokenLoginUserService.updateToken(loginUser1);
					} else {
						throw new CustomExceptionNodataFound("Something Went Wrong");
					}
					user.setLastloginDate(new Date());
					user.setAttempts(0);
					userRepository.save(user);
					result.setToken(token);
					result.setStatus(EnumConstants.SUCCESS);
					result.setMessage("Login Successfully");
					result.setStatusCode(HttpStatus.OK.value());
					return result;
				} else {
					user.setLastloginDate(new Date());
					user.getModifiedDate();
					Integer userAttempt = user.getAttempts();
					if (userAttempt < 3) {
						user.setAttempts(userAttempt + 1);
					} else {
						result.setMessage(
								"Your account is locked due to 3 incorrect attempt. Please contact admin or wait for "
										+ lockTime + " minuits");
						result.setStatusCode(HttpStatus.LOCKED.value());
						result.setStatus(EnumConstants.ERROR);
						return result;
					}
					userRepository.save(user);
					Integer remaining = 4 - (user.getAttempts());
					logger.debug("User with {} not found", request.getUsername());
					result.setStatus(EnumConstants.ERROR);
					result.setMessage("Incorrect credentials entered.Remaining attemp is: " + remaining);
					result.setStatusCode(HttpStatus.BAD_REQUEST.value());
					return result;
				}

			}

			else {

				userResponse = Optional.ofNullable(this.usersRepository.findByEmailId(request.getUsername()));
				if (userResponse.isPresent()) {
					user = userResponse.get();
				}

				System.err.println("user" + user);
				logger.debug("User found - {}", user.getEmailId());
				if (user.getLastloginDate() != null) {
					Date dt = user.getLastloginDate();
					long updatedDate = dt.getTime();
					long currentDate = new Date().getTime();
					long timeDiff = currentDate - updatedDate;
					timeDiff = timeDiff / 1000; // time difference in sec
					long lockTimeSec = lockTime * 60; // lockTime in sec

					// is user locked or not
					boolean isAccountUnlock = (lockTimeSec - timeDiff) > 0 ? false : true;
					if (isAccountUnlock && user.getAttempts() >= 3) {
						user.setAttempts(0);
						user.setLastloginDate(new Date());
						userRepository.save(user);
					}
				}
				if (request.getUsername().equals(user.getEmailId())
						&& this.passwordEncoder.matches(request.getPassword(), user.getPassword())
						&& (user.getAttempts() < 3)) {
					// Generate token
					UserDetails userDetails = this.customUserDetailService.loadUserByUsername(request.getUsername());
					String token = this.jwtHelper.generateToken(userDetails);
					TokenLoginUser loginUser1 = tokenLoginUserService.findByUsernameEmailId(request.getUsername());
					TokenLoginUser loginUserDonorId = tokenLoginUserService.findByUsernameDonorId(user.getDonorId());

					if (loginUser1 == null && loginUserDonorId == null) {
						TokenLoginUser loginUser = new TokenLoginUser();
						loginUser.setToken(token);
						loginUser.setEmailId(request.getUsername());
						loginUser.setFlag(true);
						loginUser.setLastUpdatedOn(new Date());
						tokenLoginUserService.saveUser(loginUser);

					} else if (loginUserDonorId != null && loginUserDonorId.isFlag()) {
						loginUserDonorId.setFlag(true);
						loginUserDonorId.setEmailId(request.getUsername());
						loginUserDonorId.setLastUpdatedOn(new Date());
						loginUserDonorId.setToken(token);
						tokenLoginUserService.updateToken(loginUserDonorId);
					}

					else if (loginUser1 != null && !loginUser1.isFlag()) {
						loginUser1.setFlag(true);
						loginUser1.setLastUpdatedOn(new Date());
						loginUser1.setToken(token);
						tokenLoginUserService.updateToken(loginUser1);
					} else if (loginUser1 != null && loginUser1.isFlag()) {

						loginUser1.setLastUpdatedOn(new Date());
						loginUser1.setToken(token);
						tokenLoginUserService.updateToken(loginUser1);
					} else {
						throw new CustomExceptionNodataFound("Something Went Wrong");
					}
					user.setLastloginDate(new Date());
					user.setAttempts(0);
					userRepository.save(user);
					result.setToken(token);
					result.setStatus(EnumConstants.SUCCESS);
					result.setMessage("Login Successfully");
					result.setStatusCode(HttpStatus.OK.value());
					return result;
				} else {
					user.setLastloginDate(new Date());
					user.getModifiedDate();
					Integer userAttempt = user.getAttempts();
					if (userAttempt < 3) {
						user.setAttempts(userAttempt + 1);
					} else {
						result.setMessage(
								"Your account is locked due to 3 incorrect attempt. Please contact admin or wait for "
										+ lockTime + " minuits");
						result.setStatusCode(HttpStatus.LOCKED.value());
						result.setStatus(EnumConstants.ERROR);
						return result;
					}
					userRepository.save(user);
					Integer remaining = 4 - (user.getAttempts());
					logger.debug("User with {} not found", request.getUsername());
					throw new CustomExceptionNodataFound(
							"Incorrect credentials entered.Remaining attemp is: " + remaining);
				}

			}

		} else {
			throw new CustomExceptionNodataFound("UserName or Password Should not be Null");
		}

	}

	// Email for Account activate

	public ApiResponse<String> sendEmailActivation(String email) {
		ApiResponse<String> res = new ApiResponse<>();

//		UsersRequest user = userDao.getByUserEmail(email);
//		

		// String token = this.jwtHelper.generateToken(userDetails);

		String subject = "Account Acctivation";
		String content = "Dear Sir/Madam,\n \tYou have requested to Activate your account."
				+ "Click the link below to activate your account :\n http://localhost:3000/activateuser"
				+ "\n \n Note: Link is valid for 1 hour.\n" + "";
		emailService.sendSimpleEmail(email, subject, content);

		res.setStatus(EnumConstants.SUCCESS);
		res.setStatusCode(HttpStatus.OK.value());
		return res;
	}

	// Email

	// logout
	@Override

	public ApiResponse<String> logout(LoginRequest request, String token) {
		ApiResponse<String> result = new ApiResponse<>();
		if (token != null) {
			String userName = jwtHelper.getUsernameFromToken(token);
			Users userResponseDonorId = this.userRepository.findByDonorId(request.getUsername());
			if (userResponseDonorId != null) {

				if (request.getUsername().equals(userName)) {
					this.customUserDetailService.loadUserByUsername(userName);
				}

			} else {
				userResponseDonorId = this.userRepository.findByEmailId(request.getUsername());

				if (request.getUsername().equals(userName)) {
					this.customUserDetailService.loadUserByUsername(userName);
				}
			}
			if (userResponseDonorId.getPassword() == null
					|| !this.passwordEncoder.matches(request.getPassword(), userResponseDonorId.getPassword())) {
				throw new CustomExceptionNodataFound("Invalid Credential");
			}
			TokenLoginUser tokenLoginUser = tokenLoginUserService.findByUsername(request.getUsername());
			if (tokenLoginUser != null && tokenLoginUser.isFlag()) {
				tokenLoginUser.setFlag(false);
				tokenLoginUserService.updateToken(tokenLoginUser);
			}
			if (userResponseDonorId != null) {

				this.userRepository.save(userResponseDonorId);
				result.setMessage("Logged out successfully");
				result.setStatus(EnumConstants.SUCCESS);
				result.setStatusCode(HttpStatus.OK.value());
				return result;
			}

		}

		throw new CustomExceptionNodataFound("Invalid Token");
	}

	// Reset Password
	@Override
	public ApiResponse<String> resetPassword(String formData) throws JsonProcessingException {
		ApiResponse<String> res = new ApiResponse<>();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(formData);

		String token = jsonNode.get("token").asText();
		String password = jsonNode.get("password").asText();

		Users user = null;
		String userName = jwtHelper.getUsernameFromToken(token);
		UserDetails userDetails = this.customUserDetailService.loadUserByUsername(userName);
		if (Boolean.FALSE.equals(jwtHelper.validateToken(token, userDetails))) {

			throw new CustomExceptionNodataFound("");
		}

		user = this.userRepository.findByDonorId(userName);

		if (user == null) {
			user = this.userRepository.findByEmailId(userName);
		}

		user.setPassword(this.passwordEncoder.encode(password));
		this.userRepository.save(user);
		res.setStatus(EnumConstants.SUCCESS);
		res.setMessage("You have successfully changed your password.");
		res.setStatusCode(HttpStatus.OK.value());
		return res;
	}

	@Override
	public ApiResponse<String> sendEmailPassword(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<String> loginOtp(LoginRequest request) {

		ApiResponse<String> result = new ApiResponse<>();

		if (request.getUsername() != null || request.getPassword() != null) {

			Optional<Users> userResponse = Optional
					.ofNullable(this.usersRepository.findByDonorId(request.getUsername()));

			Users user = null;
			if (userResponse.isPresent()) {
				user = userResponse.get();

				System.err.println("user" + user);
				logger.debug("User found - {}", user.getDonorId());
				if (user.getLastloginDate() != null) {
					Date dt = user.getLastloginDate();
					long updatedDate = dt.getTime();
					long currentDate = new Date().getTime();
					long timeDiff = currentDate - updatedDate;
					timeDiff = timeDiff / 1000; // time difference in sec
					long lockTimeSec = lockTime * 60; // lockTime in sec

					// is user locked or not
					boolean isAccountUnlock = (lockTimeSec - timeDiff) > 0 ? false : true;
					if (isAccountUnlock && user.getAttempts() >= 3) {
						user.setAttempts(0);
						user.setLastloginDate(new Date());
						userRepository.save(user);
					}
				}
				if (request.getUsername().equals(user.getDonorId())
						&& this.passwordEncoder.matches(request.getPassword(), user.getPassword())
						&& (user.getAttempts() < 3)) {
					// Generate OTP
					otpService.sendOtpByEmail(user.getEmailId());
					result.setStatus(EnumConstants.SUCCESS);
					result.setMessage("Otp Send Successfully");
					result.setStatusCode(HttpStatus.OK.value());
					return result;
				} else {
					user.setLastloginDate(new Date());
					user.getModifiedDate();
					Integer userAttempt = user.getAttempts();
					if (userAttempt < 3) {
						user.setAttempts(userAttempt + 1);
					} else {
						result.setMessage(
								"Your account is locked due to 3 incorrect attempt. Please contact admin or wait for "
										+ lockTime + " minuits");
						result.setStatusCode(HttpStatus.LOCKED.value());
						result.setStatus(EnumConstants.ERROR);
						return result;
					}
					userRepository.save(user);
					Integer remaining = 4 - (user.getAttempts());
					logger.debug("User with {} not found", request.getUsername());
					result.setStatus(EnumConstants.ERROR);
					result.setMessage("Incorrect credentials entered.Remaining attemp is: " + remaining);
					result.setStatusCode(HttpStatus.BAD_REQUEST.value());
					return result;
				}

			}

			else {

				userResponse = Optional.ofNullable(this.usersRepository.findByEmailId(request.getUsername()));
				if (userResponse.isPresent()) {
					user = userResponse.get();
				}

				System.err.println("user" + user);
				logger.debug("User found - {}", user.getEmailId());
				if (user.getLastloginDate() != null) {
					Date dt = user.getLastloginDate();
					long updatedDate = dt.getTime();
					long currentDate = new Date().getTime();
					long timeDiff = currentDate - updatedDate;
					timeDiff = timeDiff / 1000; // time difference in sec
					long lockTimeSec = lockTime * 60; // lockTime in sec

					// is user locked or not
					boolean isAccountUnlock = (lockTimeSec - timeDiff) > 0 ? false : true;
					if (isAccountUnlock && user.getAttempts() >= 3) {
						user.setAttempts(0);
						user.setLastloginDate(new Date());
						userRepository.save(user);
					}
				}
				if (request.getUsername().equals(user.getEmailId())
						&& this.passwordEncoder.matches(request.getPassword(), user.getPassword())
						&& (user.getAttempts() < 3)) {
					otpService.sendOtpByEmail(user.getEmailId());
					result.setStatus(EnumConstants.SUCCESS);
					result.setMessage("Otp Send Successfully");
					result.setStatusCode(HttpStatus.OK.value());
					return result;
				} else {
					user.setLastloginDate(new Date());
					user.getModifiedDate();
					Integer userAttempt = user.getAttempts();
					if (userAttempt < 3) {
						user.setAttempts(userAttempt + 1);
					} else {
						result.setMessage(
								"Your account is locked due to 3 incorrect attempt. Please contact admin or wait for "
										+ lockTime + " minuits");
						result.setStatusCode(HttpStatus.LOCKED.value());
						result.setStatus(EnumConstants.ERROR);
						return result;
					}
					userRepository.save(user);
					Integer remaining = 4 - (user.getAttempts());
					logger.debug("User with {} not found", request.getUsername());
					throw new CustomExceptionNodataFound(
							"Incorrect credentials entered.Remaining attemp is: " + remaining);
				}

			}

		} else {
			throw new CustomExceptionNodataFound("UserName or Password Should not be Null");
		}

	}
	
	public ApiResponse<String> verifyOtp(String email, String otp) {
	    ApiResponse<String> result = new ApiResponse<>();
	    Users user = findUserByDonorIdOrEmailId(email);
	    OtpModel otpModel=otpService.findByOtp(otp);
	    if (user == null || !otp.equals(otpModel.getOtpCode())) {
	        throw new CustomExceptionNodataFound("Invalid OTP");
	    }

	    UserDetails userDetails = this.customUserDetailService.loadUserByUsername(email);
	    String token = this.jwtHelper.generateToken(userDetails);
	    TokenLoginUser loginUser = tokenLoginUserService.findByUsernameDonorId(user.getDonorId());
	    TokenLoginUser loginUserEmailId = tokenLoginUserService.findByUsernameEmailId(user.getEmailId());

	    if (loginUser == null && loginUserEmailId == null) {
	        loginUser = new TokenLoginUser();
	        loginUser.setFlag(true);
	    } else if (loginUserEmailId != null && loginUserEmailId.isFlag()) {
	        loginUser = loginUserEmailId;
	        loginUser.setFlag(true);
	    } else if (loginUser != null && !loginUser.isFlag()) {
	        loginUser.setFlag(true);
	    }
	    loginUser.setLastUpdatedOn(new Date());
	    loginUser.setToken(token);
	    loginUser.setDonorId(user.getDonorId());
	    loginUser.setEmailId(user.getEmailId());
	    tokenLoginUserService.saveUser(loginUser);

	    user.setLastloginDate(new Date());
	    user.setAttempts(0);
//	    user.setOtp(null);
	    userRepository.save(user);

	    result.setToken(token);
	    result.setStatus(EnumConstants.SUCCESS);
	    result.setMessage("Login Successfully");
	    result.setStatusCode(HttpStatus.OK.value());

	    return result;
	}

	public Users findUserByDonorIdOrEmailId(String email) {
	    Users user = this.usersRepository.findByDonorId(email);
	    if (user == null) {
	        user = this.usersRepository.findByEmailId(email);
	    }
	    return user;
	}

	
	
}