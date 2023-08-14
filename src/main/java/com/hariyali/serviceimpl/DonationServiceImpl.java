package com.hariyali.serviceimpl;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hariyali.EnumConstants;
import com.hariyali.config.JwtHelper;
import com.hariyali.dao.UserDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.dto.PackagesRequest;
import com.hariyali.entity.Address;
import com.hariyali.entity.Donation;
import com.hariyali.entity.PaymentInfo;
import com.hariyali.entity.Recipient;
import com.hariyali.entity.UserPackages;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.AddressRepository;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.PaymentInfoRepository;
import com.hariyali.repository.RecipientRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.DonationService;
import com.hariyali.utils.EmailService;

@Service
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
	public ApiResponse<DonationDTO> saveUserDonations(JsonNode jsonNode, String donarID, HttpServletRequest request)
			throws JsonProcessingException {
		JsonNode userNode = jsonNode.get("user");
		ApiResponse<DonationDTO> response = null;

		JsonNode donationNode = userNode.get("donations");

		Users userEmail = this.usersRepository.findByEmailId(userNode.get("emailId").asText());

		if (userEmail == null) {
			throw new CustomExceptionNodataFound("Given Email Id doesn't exists");
		}
//		if (userEmail.getPanCard() == null || (userEmail.getPanCard()).equals("")) {
//			
//				throw new CustomExceptionNodataFound("Please enter PAN card details");
//			
////			// Check if the PAN card is already linked to another account
////			Users existingUserWithPAN = this.usersRepository.getUserByPancard(panCard);
////			if (existingUserWithPAN != null) {
////				throw new CustomException("PAN card is already linked to another account");
////			}
////			userEmail.setPanCard(panCard); 
////			this.usersRepository.save(userEmail); 
//		}

		if (donationNode == null) {
			throw new CustomException("Donation not found");
		}

		String donationMode = null;
		if (donationNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) donationNode;

			for (JsonNode donation : arrayNode) {
				if (donation.get("donationMode") != null) {
					donationMode = donation.get("donationMode").asText();
				}
			}
		}
		if (donationMode == null) {
			throw new CustomException("Donation mode not selected");
		}

		if (donationMode.equalsIgnoreCase("offline")) {
			// send email to user
			response = saveDonation(jsonNode, usersServiceImpl.generateDonorId(), request);
			return response;
		} else if (donationMode.equalsIgnoreCase("online")) {
			return saveDonation(jsonNode, donarID, request);
		} else {
			throw new CustomException("Invalid donation mode");
		}
	}

	public ApiResponse<DonationDTO> saveDonation(JsonNode jsonNode, String donarID, HttpServletRequest request) {
		ApiResponse<DonationDTO> response = new ApiResponse<>();

		JsonNode userNode = jsonNode.get("user");
		JsonNode donationNode = userNode.get("donations");
		JsonNode donationString = jsonNode.at("/user/donations/0/recipient");
		String donationMode = userNode.get("donations").get(0).get("donationMode").asText();

		if (donationNode == null) {
			throw new CustomException("Donation not found");
		}

		Users resulEntity = usersRepository.findByEmailId(userNode.get("emailId").asText());

		if (resulEntity == null)
			throw new CustomExceptionNodataFound(
					"User with " + userNode.get("emailId").asText() + " is not Registered");

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
		if (donationMode.equalsIgnoreCase("online")) {

			createdBy = userNode.get("emailId").asText();

		} else {
			createdBy = userToken.getEmailId();
		}

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		Users user = gson.fromJson(userNode.toString(), Users.class);

		// set user to donation and save donation
		if (user.getDonations() != null) {
			for (Donation donation : user.getDonations()) {
				donation.setCreatedDate(newDate);
				donation.setModifiedDate(newDate);
				donation.setCreatedBy(createdBy);
				donation.setUsers(resulEntity);
				donation.setModifiedBy(createdBy);
				donationRepository.save(donation);
				Donation resultdonation = donationRepository.getDonationByUserID(resulEntity.getUserId());

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

							if (donationString.isArray()) {
								ArrayNode arrayNode = (ArrayNode) donationString;
								for (JsonNode recipients : arrayNode) {
									String recipientEmail = recipients.get("emailId").asText();

									// Check if the email already exists
									Users existingUser = usersRepository.findByEmailId(recipientEmail);
									if (existingUser != null) {

										continue;
									}
									usersServiceImpl.saveUser(recipients, donarID, request, true, createdBy, userNode);
								}
							}
						}
					}
				}

			}
		}
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Donation added Successfully..!");
		return response;
	}

	@Override
	public Object updateUserDonations(JsonNode jsonNode, HttpServletRequest request) {
		String subject = "Updated Plant Donation Details";
		String body = "Dear User,\n We wanted to inform you that your plant donation details have been updated in our records.\\n\\n\""
				+ "Best regards,\n" + "Hariyai Team";
		JsonNode userNode = jsonNode.get("user");
		ApiResponse<DonationDTO> response = new ApiResponse<>();
		if (userNode == null) {
			throw new CustomException("No data found In User");
		}

		Users resulEntity = usersRepository.findByEmailId(userNode.get("emailId").asText());

		if (resulEntity == null)
			throw new CustomExceptionNodataFound(
					"User with " + userNode.get("emailId").asText() + " is not Registered");

		Date newDate = new Date();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		Users user = gson.fromJson(userNode.toString(), Users.class);

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

		Optional<Users> userOptional = Optional.ofNullable(this.usersRepository.findByEmailId(email));

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
		Donation d=donationRepository.findById(donationId).get();
		System.out.println(d.toString());
		return d;
	}

}
