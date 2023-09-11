package com.hariyali.serviceimpl;

import com.ccavenue.security.AesCryptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hariyali.EnumConstants;
import com.hariyali.config.JwtHelper;
import com.hariyali.dao.UserDao;
import com.hariyali.dao.paymentGateway.PaymentGatewayConfigurationDao;
import com.hariyali.dto.*;
import com.hariyali.entity.*;
import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.*;
import com.hariyali.service.DonationService;
import com.hariyali.service.ReceiptService;
import com.hariyali.utils.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Optional.*;

@Service
@Slf4j
public class DonationServiceImpl implements DonationService {

	@Autowired
	private DonationRepository donationRepository;

	@Autowired
	private PaymentInfoRepository paymentInfoRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private UserDao userDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private RecipientRepository recipientRepository;

	@Autowired
	private UserPackageRepository userPackageRepository;

	@Autowired
	@Lazy
	private UsersServiceImpl usersServiceImpl;

	@Autowired
	ReceiptService receiptService;

	@Autowired
	PaymentInfoRepository paymentIfoRepository;
	
	@Autowired
	ReceiptRepository receiptRepository;
	
	@Autowired
	private PaymentGatewayConfigurationDao gatewayConfigurationDao;

	@Override
	public ApiResponse<Object> getDonationById(int donationId) {
		ApiResponse<Object> response = new ApiResponse<>();
		Object donation = donationRepository.getSpecificDonationById(donationId);

		if (donation != null) {
			response.setData(donation);
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Donation found successfully");
			return response;
		}
		throw new CustomExceptionNodataFound("No donation found");
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public ApiResponse<DonationDTO> saveUserDonations(UsersDTO usersDTO, String donarID, HttpServletRequest request)
			throws JsonProcessingException {
//		JsonNode userNode = jsonNode.get("user");
		ApiResponse<DonationDTO> response = null;

//		JsonNode donationNode = userNode.get("donations");

		Users userEmail = this.usersRepository.findByEmailId(usersDTO.getEmailId());

		ofNullable(userEmail).orElseThrow(()->new CustomExceptionNodataFound("Given Email Id doesn't exists"));

		ofNullable(usersDTO.getDonations()).orElseThrow(()->new CustomException("Donation not found"));
//		if (donationNode == null) {
//			throw new CustomException("Donation not found");
//		}
		DonationDTO donationDTO = Optional.of(usersDTO.getDonations()).filter(donationDTOS -> !donationDTOS.isEmpty()).get().stream().findFirst().get();
		Optional.of(donationDTO).map(DonationDTO::getDonationMode).orElseThrow(()->new CustomException("Donation mode not selected"));

		if ("offline".equalsIgnoreCase(donationDTO.getDonationMode())) {
			// send email to user
			response = saveDonationOffline(usersDTO, usersServiceImpl.generateDonorId(), request);
			Receipt receipt = receiptRepository.getUserReceipt(userEmail.getUserId());
			int donationCnt=donationRepository.donationCount(userEmail.getEmailId());
				if(donationCnt>1) {
					emailService.sendReceiptWithAttachment(userEmail.getEmailId(), receipt);
				}
				else {
//					emailService.sendEmailWithAttachment(userEmail.getEmailId(), EnumConstants.subject, EnumConstants.content,
//							receipt.getReciept_Path(), userEmail);
					emailService.sendWelcomeLetterMail(userEmail.getEmailId(), EnumConstants.subject, EnumConstants.content, userEmail);
					emailService.sendReceiptWithAttachment(userEmail.getEmailId(),receipt);
				}

			return response;
		} else if ("online".equalsIgnoreCase(donationDTO.getDonationMode())) {
			return saveDonation(usersDTO, donarID, request);
		} else {
			throw new CustomException("Invalid donation mode");
		}
	}

	private ApiResponse<DonationDTO> saveDonationOffline(UsersDTO usersDTO, String donarID,
			HttpServletRequest request) {
		ApiResponse<DonationDTO> response = new ApiResponse<>();
//		String donationId = null;
//		JsonNode userNode = jsonNode.get("user");
//		JsonNode donationNode = userNode.get("donations");
//		JsonNode donationString = jsonNode.at("/user/donations/0/recipient");
		String donationMode = usersDTO.getDonations().get(0).getDonationMode();;
		DonationDTO donationDTO = new DonationDTO();

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		ofNullable(resulEntity).orElseThrow(() -> new CustomExceptionNodataFound(
				"User with " + usersDTO.getEmailId() + " is not Registered"));

		Date newDate = new Date();

		String token = null;
		String userName = null;
		Users userToken = null;
		if (request != null) {
			token = request.getHeader("Authorization");
			userName = jwtHelper.getUsernameFromToken(token.substring(7));
			userToken = this.usersRepository.findByEmailId(userName);
		}

		String createdBy = null;

		// set created by based on donationMode
		if ("online".equalsIgnoreCase(donationMode)) {
			createdBy = usersDTO.getEmailId();
		} else {
			createdBy = userToken.getEmailId();
		}

		Users user = modelMapper.map(usersDTO, Users.class);

		// set user to donation and save donation
		if (user.getDonations() != null) {
			for (Donation donation : user.getDonations()) {
				donation.setCreatedDate(newDate);
				donation.setModifiedDate(newDate);
				donation.setCreatedBy(createdBy);
				donation.setUsers(resulEntity);
				donation.setModifiedBy(createdBy);
				Donation resultdonation = donationRepository.save(donation);
				//resultdonation = donationRepository.getDonationByUserID(resulEntity.getUserId());
				donationDTO.setDonationId(donation.getDonationId());
				donationDTO.setCreatedDate(newDate);
				donationDTO.setCreatedBy(createdBy);
				donationDTO.setDonationEvent(donation.getDonationEvent());
				donationDTO.setDonationMode(donation.getDonationMode());
				donationDTO.setTotalAmount(donation.getTotalAmount());
				// set paymentInfo donation wise
				if (donation.getPaymentInfo() != null) {
					for (PaymentInfo paymentInfo : donation.getPaymentInfo()) {
						paymentInfo.setCreatedDate(newDate);
						paymentInfo.setModifiedDate(newDate);
						paymentInfo.setCreatedBy(createdBy);
						paymentInfo.setModifiedBy(createdBy);
						paymentInfo.setPaymentStatus(EnumConstants.PAYMENT_COMPLETED);
						paymentInfo.setUserDonation(resultdonation);
						paymentInfoRepository.save(paymentInfo);

					}
					donationDTO.setPaymentInfo(donation.getPaymentInfo());
				}

				// set donation to user package and save user package
				if (donation.getUserPackage() != null) {
					for (UserPackages userPackage : donation.getUserPackage()) {
						userPackage.setCreatedDate(newDate);
						userPackage.setModifiedDate(newDate);
						userPackage.setCreatedBy(createdBy);
						userPackage.setModifiedBy(createdBy);
						userPackage.setUserDonation(resultdonation);
						userPackageRepository.save(userPackage);
					}
					donationDTO.setUserPackage(donation.getUserPackage());
				}

				// set donation to recipient and save recipient
				if (donation.getRecipient() != null) {
					for (Recipient recipient : donation.getRecipient()) {
						recipient.setCreatedDate(newDate);
						recipient.setModifiedDate(newDate);
						recipient.setCreatedBy(createdBy);
						recipient.setModifiedBy(createdBy);
						recipient.setUserDonation(resultdonation);
						recipientRepository.save(recipient);

						Recipient resultRecipient = recipientRepository
								.getRecipientByDonationId(resultdonation.getDonationId());

						// set recipient to address and save address
						if (recipient.getAddress() != null) {
							for (Address address : recipient.getAddress()) {
								address.setCreatedDate(newDate);
								address.setModifiedDate(newDate);
								address.setCreatedBy(createdBy);
								address.setModifiedBy(createdBy);
								address.setRecipient(resultRecipient);
								addressRepository.save(address);
							}

							if (!isNull(usersDTO.getDonations().get(0).getRecipient()) && !usersDTO.getDonations().get(0).getRecipient().isEmpty()) {
								for (Recipient recipients : usersDTO.getDonations().get(0).getRecipient()) {
									String recipientEmail = recipients.getEmailId();

									// Check if the email already exists
									Users existingUser = usersRepository.findByEmailId(recipientEmail);
									if (existingUser != null) {

										continue;
									}
									usersServiceImpl.saveUser(toUsersDTO(recipients), donarID, request, true, createdBy, donationMode);
								}
							}
						}
						Users recipientData = usersRepository.findByEmailId(recipient.getEmailId());
						emailService.sendGiftingLetterEmail(recipientData,donation.getDonationEvent());

					}

				}
				donation.setRecipient(donation.getRecipient());
			}
		}
		Donation donation = donationRepository.getById(donationDTO.getDonationId());
		String paymentStatus = paymentIfoRepository
				.getPaymentStatusByDonationId(donationDTO.getDonationId());
		if (paymentStatus.equalsIgnoreCase("Completed")) {
			receiptService.generateReceipt(donation);
		}
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Donation added Successfully..!");
		return response;
	}

	public ApiResponse<DonationDTO> saveDonation(UsersDTO usersDTO, String donarID, HttpServletRequest request) {
		ApiResponse<DonationDTO> response = new ApiResponse<>();

//		JsonNode userNode = jsonNode.get("user");
//		JsonNode donationNode = userNode.get("donations");
//		JsonNode donationString = jsonNode.at("/user/donations/0/recipient");
		String donationMode = usersDTO.getDonations().get(0).getDonationMode();

//		if (donationNode == null) {
//			throw new CustomException("Donation not found");
//		}

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		if (resulEntity == null)
			throw new CustomExceptionNodataFound(
					"User with " + usersDTO.getEmailId() + " is not Registered");

		Date newDate = new Date();

		String token = null;
		String userName = null;
		Users userToken = null;
		if (request != null) {
			token = request.getHeader("Authorization");
			userName = jwtHelper.getUsernameFromToken(token.substring(7));
			userToken = this.usersRepository.findByEmailId(userName);
		}

		String createdBy;

		// set created by based on donationMode
		if (donationMode.equalsIgnoreCase("online")) {
			createdBy = usersDTO.getEmailId();
		} else {
			createdBy = userToken.getEmailId();
		}

		Users user = modelMapper.map(usersDTO, Users.class);
		Double totalAmount = 0.0;
		Long orderId = Calendar.getInstance().getTimeInMillis();
		// set user to donation and save donation
		if (user.getDonations() != null) {
			for (Donation donation : user.getDonations()) {
				donation.setCreatedDate(newDate);
				donation.setModifiedDate(newDate);
				donation.setCreatedBy(createdBy);
				donation.setUsers(resulEntity);
				donation.setModifiedBy(createdBy);
				donation.setOrderId(orderId.toString());
				totalAmount = donation.getTotalAmount();
				donation = donationRepository.save(donation);
				Donation resultdonation = donationRepository.getDonationByUserID(resulEntity.getUserId());

				// set paymentInfo donation wise
//				if (donation.getPaymentInfo() != null) {
//					for (PaymentInfo paymentInfo : donation.getPaymentInfo()) {
//						paymentInfo.setCreatedDate(newDate);
//						paymentInfo.setModifiedDate(newDate);
//						paymentInfo.setCreatedBy(createdBy);
//						paymentInfo.setModifiedBy(createdBy);
//						paymentInfo.setPaymentStatus(EnumConstants.PAYMENT_COMPLETED);
//						paymentInfo.setUserDonation(resultdonation);
//						paymentInfoRepository.save(paymentInfo);
//					}
//				}

				// set donation to user package and save user package
				if (donation.getUserPackage() != null) {
					for (UserPackages userPackage : donation.getUserPackage()) {
						userPackage.setCreatedDate(newDate);
						userPackage.setModifiedDate(newDate);
						userPackage.setCreatedBy(createdBy);
						userPackage.setModifiedBy(createdBy);
						userPackage.setUserDonation(resultdonation);
						userPackageRepository.save(userPackage);
					}
				}

				// set donation to recipient and save recipient
				if (donation.getRecipient() != null) {
					for (Recipient recipient : donation.getRecipient()) {
						recipient.setCreatedDate(newDate);
						recipient.setModifiedDate(newDate);
						recipient.setCreatedBy(createdBy);
						recipient.setModifiedBy(createdBy);
						recipient.setUserDonation(resultdonation);
						recipientRepository.save(recipient);

						Recipient resultRecipient = recipientRepository
								.getRecipientByDonationId(resultdonation.getDonationId());

						// set recipient to address and save address
						if (recipient.getAddress() != null) {
							for (Address address : recipient.getAddress()) {
								address.setCreatedDate(newDate);
								address.setModifiedDate(newDate);
								address.setCreatedBy(createdBy);
								address.setModifiedBy(createdBy);
								address.setRecipient(resultRecipient);
								addressRepository.save(address);
							}

							if (!isNull(usersDTO.getDonations().get(0).getRecipient()) && !usersDTO.getDonations().get(0).getRecipient().isEmpty()) {
								for (Recipient recipients : usersDTO.getDonations().get(0).getRecipient()) {
									String recipientEmail = recipients.getEmailId();

									// Check if the email already exists
									Users existingUser = usersRepository.findByEmailId(recipientEmail);
									if (existingUser != null) {

										continue;
									}

									usersServiceImpl.saveUser(toUsersDTO(recipients), donarID, request, true, createdBy, donationMode);
								}
							}
						}
					}
				}

			}
		}

		if ("online".equalsIgnoreCase(donationMode)) {
			// get payment gateway configuration for CCAVENUE
			PaymentGatewayConfiguration gatewayConfiguration = gatewayConfigurationDao.findByGatewayName("CCAVENUE");


//			Users userPay = new GsonBuilder().create().fromJson(usersRepository.getUserByEmail(user.getEmailId()).toString(), Users.class);

			String queryString = "";
			queryString += "merchant_id=" + gatewayConfiguration.getMerchantId();
			queryString += "&order_id=" + orderId;
			queryString += "&currency=INR";
			queryString += "&amount=" + totalAmount;
			queryString += "&redirect_url=" + gatewayConfiguration.getRedirectURL();
			queryString += "&cancel_url=" + gatewayConfiguration.getRedirectURL();
			queryString += "&language=EN";
			queryString += "&billing_name=" + usersDTO.getFirstName() + " " + usersDTO.getLastName();
			AddressDTO address = ofNullable(usersDTO.getAddress()).stream().filter(addresses -> !addresses.isEmpty()).findFirst().get().get(0);
			queryString += "&billing_address=" + address.getStreet1() + " " + address.getStreet2() + " "+ address.getStreet3();
			queryString += "&billing_city=" + address.getCity();
			queryString += "&billing_state=" + address.getState();
			queryString += "&billing_zip=" + address.getPostalCode();
			queryString += "&billing_country=" + address.getCountry();
			queryString += "&billing_tel=" + usersDTO.getMobileNo();
			queryString += "&billing_email=" + usersDTO.getEmailId();
			AesCryptUtil aesUtil=new AesCryptUtil (gatewayConfiguration.getAccessKey());
			String encRequest=aesUtil.encrypt(queryString);
			response.setAccessCode(gatewayConfiguration.getAccessCode());
			response.setEncRequest(encRequest);
			response.setGatewayURL(gatewayConfiguration.getGatewayURL());
		}
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Donation added Successfully..!");
		return response;
	}

	private UsersDTO toUsersDTO(Recipient recipients){
		UsersDTO usersDTO = new UsersDTO();
		usersDTO.setFirstName(recipients.getFirstName());
		usersDTO.setLastName(recipients.getLastName());
		usersDTO.setMobileNo(recipients.getMobileNo());
		usersDTO.setEmailId(recipients.getEmailId());
		usersDTO.setAddress(ofNullable(recipients.getAddress())
				.stream()
				.map(address -> modelMapper.map(address, AddressDTO.class)).collect(Collectors.toList()));
		return usersDTO;
	}

	@Override
	public Object updateUserDonations(UsersDTO usersDTO, HttpServletRequest request) {
		String subject = "Updated Plant Donation Details";
		String body = "Dear User,\n We wanted to inform you that your plant donation details have been updated in our records.\\n\\n\""
				+ "Best regards,\n" + "Hariyai Team";

		ApiResponse<DonationDTO> response = new ApiResponse<>();
		ofNullable(usersDTO).orElseThrow(()->new CustomException("No data found In User"));

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		ofNullable(resulEntity).orElseThrow(()-> new CustomExceptionNodataFound(
				"User with " + usersDTO.getEmailId() + " is not Registered"));


		Date newDate = new Date();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));


		Gson gson = new GsonBuilder()
	            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
	            .create();
		Users user = modelMapper.map(usersDTO, Users.class);

		Users userToken = usersRepository.findByEmailId(userName);
		// set user to donation and save donation
		if (user.getDonations() != null) {
			user.getDonations().forEach(donation -> {
				donation.setModifiedDate(newDate);
				donation.setModifiedBy(userToken.getEmailId());
				donation.setUsers(resulEntity);
				donationRepository.save(donation);
				Donation resultdonation = donationRepository.getDonationLatestUpdateByUserId(resulEntity.getUserId());

				// set paymentInfo donation wise
				if (donation.getPaymentInfo() != null) {
					donation.getPaymentInfo().forEach(paymentInfo -> {
						paymentInfo.setModifiedDate(newDate);
						paymentInfo.setModifiedBy(userToken.getEmailId());
						paymentInfo.setPaymentStatus(EnumConstants.PAYMENT_COMPLETED);
						paymentInfo.setUserDonation(resultdonation);
						paymentInfoRepository.save(paymentInfo);
					});
				}

				// set donation to user package and save user package
				if (donation.getUserPackage() != null) {
					donation.getUserPackage().forEach(userPackage -> {
						userPackage.setModifiedDate(newDate);
						userPackage.setModifiedBy(userToken.getEmailId());
						userPackage.setUserDonation(resultdonation);
						userPackageRepository.save(userPackage);
					});
				}

				// set donation to recipient and save recipient
				if (donation.getRecipient() != null) {
					donation.getRecipient().forEach(recipient -> {
						recipient.setModifiedDate(newDate);
						recipient.setModifiedBy(userToken.getEmailId());
						recipient.setUserDonation(resultdonation);
						recipientRepository.save(recipient);

						Recipient resultRecipient = recipientRepository
								.getLatestRecipientByDonationId(resultdonation.getDonationId());

						// set recipient to address and save address
						if (recipient.getAddress() != null) {
							recipient.getAddress().forEach(address -> {
								address.setModifiedDate(newDate);
								address.setModifiedBy(userToken.getEmailId());
								address.setRecipient(resultRecipient);
								addressRepository.save(address);
							});
						}

						emailService.sendSimpleEmail(recipient.getEmailId(), subject, body);
					});

				}

			});
		}
		emailService.sendSimpleEmail(user.getEmailId(), subject, body);
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Donation updated Successfully..!");
		return response;
	}

	@Override
	public ApiResponse<Donation> getDonation(int donationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<Object> getAllDonationDoneByUser(String email) {
		ApiResponse<Object> response = new ApiResponse<>();

		Optional<Users> userOptional = ofNullable(this.usersRepository.findByEmailId(email));

		if (userOptional.isPresent()) {
			Object result = donationRepository.getAllDonationDoneByUser(email);

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
	public ApiResponse<DonationDTO> searchDonationById(int donationId) {
		ApiResponse<DonationDTO> response = new ApiResponse<>();
		Donation result = donationRepository.findByDonationId(donationId);
		response.setData(modelMapper.map(result, DonationDTO.class));
		response.setStatusCode(HttpStatus.OK.value());
		response.setStatus(EnumConstants.SUCCESS);
		return response;

	}

	@Override
	public Donation searchDonationById1(int donationId) {
		Donation d = donationRepository.findById(donationId).get();
		log.info(d.toString());
		return d;
	}

	@Override
	public ApiResponse<List<DonationDTO>> getDonations(DonorListRequestDTO requestDTO) {
		ApiResponse<List<DonationDTO>> response = new ApiResponse<>();
		Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize());
		Page<Donation> result = donationRepository.findByUserId(requestDTO.getUserId(), pageable);
		if (!isNull(result) && !result.getContent().isEmpty()) {
			List<DonationDTO> donationDTOS = of(result.getContent()).get().stream()
					.map(data -> modelMapper.map(data, DonationDTO.class)).collect(Collectors.toList());
			response.setData(donationDTOS);
			response.setTotalPages(result.getTotalPages());
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;
		} else
			throw new CustomException("No donation found!!");
	}

}
