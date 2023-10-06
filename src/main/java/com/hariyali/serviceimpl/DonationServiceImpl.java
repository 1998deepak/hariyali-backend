package com.hariyali.serviceimpl;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import net.sf.jasperreports.engine.JREmptyDataSource;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.ccavenue.security.AesCryptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hariyali.EnumConstants;
import com.hariyali.config.JwtHelper;
import com.hariyali.dao.UserDao;
import com.hariyali.dao.paymentGateway.PaymentGatewayConfigurationDao;
import com.hariyali.dto.AddressDTO;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.DonationDTO;
import com.hariyali.dto.DonorListRequestDTO;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Address;
import com.hariyali.entity.Donation;
import com.hariyali.entity.PaymentInfo;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Recipient;
import com.hariyali.entity.UserPackages;
import com.hariyali.entity.Users;
import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.AddressRepository;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.PaymentInfoRepository;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.repository.RecipientRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.DonationService;
import com.hariyali.service.ReceiptService;
import com.hariyali.utils.AES;
import com.hariyali.utils.CommonService;
import com.hariyali.utils.EmailService;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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

	@Autowired
	CommonService commonService;

	@Value("${jasper.filepath}")
	String jasperFilePath;

	@Value("${jasper.imagespath}")
	String jasperImagesPath;

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
		ApiResponse<DonationDTO> response = null;

		Users userEmail = this.usersRepository.findByEmailId(usersDTO.getEmailId());

		ofNullable(userEmail).orElseThrow(() -> new CustomExceptionNodataFound("Given Email Id doesn't exists"));

		ofNullable(usersDTO.getDonations()).orElseThrow(() -> new CustomException("Donation not found"));

		DonationDTO donationDTO = Optional.of(usersDTO.getDonations()).filter(donationDTOS -> !donationDTOS.isEmpty())
				.get().stream().findFirst().get();
		Optional.of(donationDTO).map(DonationDTO::getDonationMode)
				.orElseThrow(() -> new CustomException("Donation mode not selected"));

		if ("offline".equalsIgnoreCase(donationDTO.getDonationMode())) {
			// send email to user
			response = saveDonationOffline(usersDTO, commonService.createDonarIDORDonationID("user"), request);
			DonationDTO donationDto = response.getData();
			Receipt receipt = receiptRepository.getUserReceipt(userEmail.getUserId());
			int donationCnt = donationRepository.donationCount(userEmail.getEmailId());
			if (donationCnt > 1) {
				emailService.sendReceiptWithAttachment(userEmail, donationDto.getOrderId(), receipt);
				emailService.sendThankyouLatter(userEmail.getEmailId(), userEmail);
			} else {
				emailService.sendWelcomeLetterMail(userEmail.getEmailId(), EnumConstants.subject, EnumConstants.content,
						userEmail);
				emailService.sendReceiptWithAttachment(userEmail, donationDto.getOrderId(), receipt);
				emailService.sendThankyouLatter(userEmail.getEmailId(), userEmail);
			}
//			if ("gift-donate".equalsIgnoreCase(donationDto.getDonationType())) {
//				String fullNameOfDonar = usersDTO.getFirstName() + " " + usersDTO.getLastName();
//				Map<String, String> responseCertifiate = generateCertificate(
//						donationDto.getRecipient().get(0).getFirstName(), donationDto.getGiftContent(), donationDto.getDonationEvent(),
//						fullNameOfDonar, donationDto.getRecipient().get(0).getEmailId());
//				emailService.sendGiftingLetterEmail(modelMapper.map(donationDto, Donation.class), null, donationDto.getDonationEvent(),
//						responseCertifiate.get("outputFile"));
//			}

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

		String donationMode = usersDTO.getDonations().get(0).getDonationMode();
		;
		DonationDTO donationDTO = new DonationDTO();

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		ofNullable(resulEntity).orElseThrow(
				() -> new CustomExceptionNodataFound("User with " + usersDTO.getEmailId() + " is not Registered"));

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
				donation.setApprovalStatus("Approved");
				donation.setApprovalDate(newDate);
				donation.setIsApproved(true);
				donation.setIsDeleted(false);
				donation.setDonationDate(newDate);
				donation.setDonationCode(commonService.createDonarIDORDonationID("donation"));
				Donation resultdonation = donationRepository.save(donation);
				// resultdonation =
				// donationRepository.getDonationByUserID(resulEntity.getUserId());
				donationDTO.setDonationId(donation.getDonationId());
				donationDTO.setCreatedDate(newDate);
				donationDTO.setCreatedBy(createdBy);
				donationDTO.setGiftContent(donation.getGiftContent());
				donationDTO.setDonationEvent(donation.getDonationEvent());
				donationDTO.setDonationMode(donation.getDonationMode());
				donationDTO.setTotalAmount(donation.getTotalAmount());
				donationDTO.setOrderId(donation.getOrderId());
				donationDTO.setDonationCode(donation.getDonationCode());
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

							if (!isNull(usersDTO.getDonations().get(0).getRecipient())
									&& !usersDTO.getDonations().get(0).getRecipient().isEmpty()) {
								for (Recipient recipients : usersDTO.getDonations().get(0).getRecipient()) {
									String recipientEmail = recipients.getEmailId();

									// Check if the email already exists
									Users existingUser = usersRepository.findByEmailId(recipientEmail);
									if (existingUser != null) {

										continue;
									}
									usersServiceImpl.saveUser(toUsersDTO(recipients), donarID, request, true, createdBy,
											donationMode);
								}
							}
						}
						Users recipientData = usersRepository.findByEmailId(donation.getRecipient().get(0).getEmailId());
						String fullNameOfDonar = resulEntity.getFirstName() + " " + resulEntity.getLastName();

						Map<String, String> responseCertifiate = generateCertificate(donation.getRecipient().get(0).getFirstName(),
								donationDTO.getGiftContent(), donationDTO.getDonationEvent(), fullNameOfDonar,
								resulEntity.getEmailId());

						commonService.saveDocumentDetails("DOCUMENT", responseCertifiate.get("filePath"),
								responseCertifiate.get("outputFile"), "PDF", "CERTIFICATE", donation);
//						emailService.sendWelcomeLetterMail(recipientData.getEmailId(), EnumConstants.subjectGiftee,
//								EnumConstants.contentGiftee, recipientData);
						emailService.sendGiftingLetterEmail(donation,recipientData, donation.getDonationEvent(),
								responseCertifiate.get("outputFile"));

					}

				}
				donation.setRecipient(donation.getRecipient());
			}
		}
		Donation donation = donationRepository.getById(donationDTO.getDonationId());
		String paymentStatus = paymentIfoRepository.getPaymentStatusByDonationId(donationDTO.getDonationId());
		if (paymentStatus.equalsIgnoreCase("Completed") || "Success".equalsIgnoreCase(paymentStatus) ) {
			receiptService.generateReceipt(donation);
		}
		response.setData(donationDTO);
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Donation added Successfully..!");
		return response;
	}

	public ApiResponse<DonationDTO> saveDonation(UsersDTO usersDTO, String donarID, HttpServletRequest request) {
		ApiResponse<DonationDTO> response = new ApiResponse<>();

		String donationMode = usersDTO.getDonations().get(0).getDonationMode();

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		if (resulEntity == null)
			throw new CustomExceptionNodataFound("User with " + usersDTO.getEmailId() + " is not Registered");

		Date newDate = new Date();

		String token = null;
		String userName = null;
		Users userToken = null;
		if (request != null) {
			token = request.getHeader("Authorization");
			if (!isNull(token)) {
				userName = jwtHelper.getUsernameFromToken(token.substring(7));
				userToken = this.usersRepository.findByEmailId(userName);
			}
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
				donation.setDonationDate(newDate);
				donation.setDonationCode(commonService.createDonarIDORDonationID("donation"));
				totalAmount = donation.getTotalAmount();

				if (usersDTO.getMeconnectId() != null) {
					if (!usersDTO.getMeconnectId().isEmpty()) {
						String str = AES.decrypt(usersDTO.getMeconnectId());
						String[] parts = str.split("\\|\\|");
						System.out.println(parts[0] + ":=>" + parts[1]);

						Integer meconnectId = Integer.parseInt(parts[0]);
						String source = new String(parts[1]);
						donation.setMeconnectId(meconnectId);
						donation.setSource(source);
					}
				}
				donation.setApprovalStatus("Pending");
				donation.setIsApproved(false);
				donation.setIsDeleted(false);
				donation = donationRepository.save(donation);
				// Donation resultdonation =
				// donationRepository.getDonationByUserID(resulEntity.getUserId());

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
						userPackage.setUserDonation(donation);
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
						recipient.setUserDonation(donation);
						recipientRepository.save(recipient);

						Recipient resultRecipient = recipientRepository
								.getRecipientByDonationId(donation.getDonationId());

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

							if (!isNull(usersDTO.getDonations().get(0).getRecipient())
									&& !usersDTO.getDonations().get(0).getRecipient().isEmpty()) {
								for (Recipient recipients : usersDTO.getDonations().get(0).getRecipient()) {
									String recipientEmail = recipients.getEmailId();

									// Check if the email already exists
									Users existingUser = usersRepository.findByEmailId(recipientEmail);
									if (existingUser != null) {
										request.getSession().setAttribute(orderId+"",recipients);
										continue;
									}

									usersServiceImpl.saveUser(toUsersDTO(recipients), donarID, request, true, createdBy,
											donationMode);
								}
							}
						}
					}
				}

			}
		}
		if (usersDTO != null) {
			Users users = usersRepository.findByEmailId(usersDTO.getEmailId());
			if (users != null) {
				if(("Corporate").equalsIgnoreCase(usersDTO.getDonarType())){
					if (!("INDIA").equalsIgnoreCase(usersDTO.getAddress().stream().map(a->a.getCountry()).findFirst().get())) {
						response.setStatus(EnumConstants.OTHERTHANINDIA);
						response.setGatewayURL("/FcraAccount");
						DonationDTO donationDTO = new DonationDTO();
						Double amount=usersDTO.getDonations().stream().map(d->d.getUserPackage()).findFirst().get().stream().map(u->u.getAmount()).findFirst().get();
						donationDTO.setTotalAmount(amount);
						donationDTO.setCreatedBy(usersDTO.getAddress().stream().map(a->a.getCountry()).findFirst().get());
						response.setData(donationDTO);
						// removing password for FCRA USER
						users.setPassword(null);
						usersRepository.save(users);
						return response;
					}
				}
				if(("Individual").equalsIgnoreCase(usersDTO.getDonarType())){
				 if (!("INDIA").equalsIgnoreCase(usersDTO.getCitizenship())) {
					response.setStatus(EnumConstants.OTHERTHANINDIA);
					response.setGatewayURL("/FcraAccount");
					DonationDTO donationDTO = new DonationDTO();
					Double amount=usersDTO.getDonations().stream().map(d->d.getUserPackage()).findFirst().get().stream().map(u->u.getAmount()).findFirst().get();
					donationDTO.setTotalAmount(amount);
					donationDTO.setCreatedBy(usersDTO.getCitizenship());
					response.setData(donationDTO);
					// removing password for FCRA USER
					users.setPassword(null);
					usersRepository.save(users);
					return response;
				}
			  }
			} else {
				if (usersDTO.getCitizenship() != null || usersDTO.getCountry() != null) {
					if(("Corporate").equalsIgnoreCase(usersDTO.getDonarType())){
						if (!("INDIA").equalsIgnoreCase(usersDTO.getAddress().stream().map(a->a.getCountry()).findFirst().get())) {
							response.setStatus(EnumConstants.OTHERTHANINDIA);
							response.setGatewayURL("/FcraAccount");
							DonationDTO donationDTO = new DonationDTO();
							Double amount=usersDTO.getDonations().stream().map(d->d.getUserPackage()).findFirst().get().stream().map(u->u.getAmount()).findFirst().get();
							donationDTO.setTotalAmount(amount);
							donationDTO.setCreatedBy(usersDTO.getAddress().stream().map(a->a.getCountry()).findFirst().get());
							response.setData(donationDTO);
							// removing password for FCRA USER
							users.setPassword(null);
							usersRepository.save(users);
							return response;
						}
					}
					if(("Individual").equalsIgnoreCase(usersDTO.getDonarType())){
					 if (!("INDIA").equalsIgnoreCase(usersDTO.getCitizenship())) {
						response.setStatus(EnumConstants.OTHERTHANINDIA);
						response.setGatewayURL("/FcraAccount");
						DonationDTO donationDTO = new DonationDTO();
						Double amount=usersDTO.getDonations().stream().map(d->d.getUserPackage()).findFirst().get().stream().map(u->u.getAmount()).findFirst().get();
						donationDTO.setTotalAmount(amount);
						donationDTO.setCreatedBy(usersDTO.getCitizenship());
						response.setData(donationDTO);
						// removing password for FCRA USER
						users.setPassword(null);
						usersRepository.save(users);
						return response;
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
			queryString += "&billing_name=" + ofNullable(usersDTO.getFirstName()).filter(StringUtils::isNotEmpty)
					.orElse(resulEntity.getFirstName()) + " " + ofNullable(usersDTO.getLastName())
					.filter(StringUtils::isNotEmpty).orElse(resulEntity.getLastName());
			AddressDTO address = ofNullable(usersDTO.getAddress()).orElse(resulEntity.getAddress().stream()
					.map(addressEntity -> modelMapper.map(addressEntity, AddressDTO.class))
					.collect(Collectors.toList())).stream().findFirst().get();
			queryString += "&billing_address=" + address.getStreet1() + " " + address.getStreet2() + " "
					+ address.getStreet3();
			queryString += "&billing_city=" + address.getCity();
			queryString += "&billing_state=" + address.getState();
			queryString += "&billing_zip=" + address.getPostalCode();
			queryString += "&billing_country=" + StringUtils.capitalize(ofNullable(address.getCountry()).orElse("").toLowerCase());
			queryString += "&billing_tel=" + ofNullable(usersDTO.getMobileNo()).filter(StringUtils::isNotEmpty).orElse(resulEntity.getMobileNo());
			queryString += "&billing_email=" + ofNullable(usersDTO.getEmailId()).filter(StringUtils::isNotEmpty).orElse(resulEntity.getEmailId());
			log.info(queryString);
			AesCryptUtil aesUtil = new AesCryptUtil(gatewayConfiguration.getAccessKey());
			String encRequest = aesUtil.encrypt(queryString);
			response.setAccessCode(gatewayConfiguration.getAccessCode());
			response.setEncRequest(encRequest);
			response.setGatewayURL(gatewayConfiguration.getGatewayURL());
		}
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Donation added Successfully..!");
		return response;
	}

	private UsersDTO toUsersDTO(Recipient recipients) {
		UsersDTO usersDTO = new UsersDTO();
		usersDTO.setFirstName(recipients.getFirstName());
		usersDTO.setLastName(recipients.getLastName());
		usersDTO.setMobileNo(recipients.getMobileNo());
		usersDTO.setEmailId(recipients.getEmailId());
		usersDTO.setAddress(ofNullable(recipients.getAddress()).stream()
				.map(address -> modelMapper.map(address, AddressDTO.class)).collect(Collectors.toList()));
		return usersDTO;
	}

	@Override
	public Object updateUserDonations(UsersDTO usersDTO, HttpServletRequest request) {
		String subject = "Updated Plant Donation Details";
		String body = "Dear User,\n We wanted to inform you that your plant donation details have been updated in our records.\\n\\n\""
				+ "Best regards,\n" + "Hariyai Team";

		ApiResponse<DonationDTO> response = new ApiResponse<>();
		ofNullable(usersDTO).orElseThrow(() -> new CustomException("No data found In User"));

		Users resulEntity = usersRepository.findByEmailId(usersDTO.getEmailId());

		ofNullable(resulEntity).orElseThrow(
				() -> new CustomExceptionNodataFound("User with " + usersDTO.getEmailId() + " is not Registered"));

		Date newDate = new Date();
		String token = request.getHeader("Authorization");
		String userName = jwtHelper.getUsernameFromToken(token.substring(7));

		Gson gson = new GsonBuilder().registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY).create();
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
			response.setTotalRecords(result.getTotalElements());
			response.setStatus(EnumConstants.SUCCESS);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Data fetched successfully..!!");
			return response;
		} else
			throw new CustomException("No donation found!!");
	}

	@Override
	public Map<String, String> generateCertificate(String recipientName, String messageContent, String donationEvent,
			String donarName, String emailID) {

		String filepath = null;
		String reportName = null;
		String imagesPathName = null;
		Map<String, String> response = new HashMap<>();

		try {

			if (donationEvent.equalsIgnoreCase("Special day")) {
				reportName = "SpecialDay.jrxml";
				imagesPathName = jasperImagesPath + File.separator + "specialDay.jpg";
			} else if (donationEvent.equalsIgnoreCase("Festivals")) {
				reportName = "Festival.jrxml";
				imagesPathName = jasperImagesPath + File.separator + "festival.jpg";

			} else if (donationEvent.equalsIgnoreCase("Achievements")) {
				reportName = "Achievement.jrxml";
				imagesPathName = jasperImagesPath + File.separator + "Achievement.jpg";

			} else if (donationEvent.equalsIgnoreCase("Memorial Tribute")) {
				reportName = "MemorialTribute.jrxml";
				imagesPathName = jasperImagesPath + File.separator + "memorialTribute.jpg";

			} else if (donationEvent.equalsIgnoreCase("Simple Donation")) {
				reportName = "SimpleDonation.jrxml";
				imagesPathName = jasperImagesPath + File.separator + "simpleDonation.png";

			}

			Map<String, Object> parameters = new HashMap<String, Object>();

			if (donationEvent.equalsIgnoreCase("Simple Donation")) {
				parameters.put("firstName", donarName);

			}

			filepath = jasperFilePath + reportName;
			parameters.put("RecipientName", recipientName);
			parameters.put("messageContent", messageContent);
			parameters.put("donarName", donarName);
			parameters.put("ImageParameter", imagesPathName);
//			filepath="\\hariyali-backend\\src\\main\\resources\\META-INF\\jasperReports\\Festival.jrxml";
			JasperReport jasperReport = JasperCompileManager.compileReport(filepath);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

			File outputFile = null;
			String path = commonService.getDonarFileFilePath(emailID);
			if (path != null) {
				String pdfFilePath = path + File.separator + donationEvent + ".pdf";
				log.info("Pdf file path=>" + pdfFilePath);
				outputFile = new File(pdfFilePath);
				JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath); // Export to PDF
				System.err.println(outputFile.getName());
				response.put("filePath", outputFile.getName());
				response.put("outputFile", outputFile.toString());

			}

		} catch (Exception e) {
			log.info(e.getMessage());
			throw new CustomException(e.getMessage());
		}

		return response;
	}

	@Override
	@Transactional
	public ApiResponse<String> approveUserDonation(DonationDTO dto, String userName) {
		ApiResponse<String> response = new ApiResponse<>();
		Donation donation = donationRepository.findByDonationId(dto.getDonationId());
		donation.setModifiedBy(userName);
		donation.setApprovalDate(new Date());
		donation.setIsApproved(ofNullable(dto.getIsApproved()).orElse(false));
		donation.setRemark(dto.getRemark());
		donation.setApprovalStatus(dto.getApprovalStatus());
		donationRepository.save(donation);
		if(donation.getIsApproved()) {

			String paymentStatus = paymentIfoRepository.getPaymentStatusByDonationId(donation.getDonationId());
			if ("Completed".equalsIgnoreCase(paymentStatus) || "Success".equalsIgnoreCase(paymentStatus)) {
				receiptService.generateReceipt(donation);
				Receipt receipt = receiptRepository.findByDonation(donation);
				emailService.sendReceiptWithAttachment(donation.getUsers(), donation.getOrderId(), receipt);
				emailService.sendThankyouLatter(donation.getUsers().getEmailId(), donation.getUsers());
			}

			response.setMessage("Donation approved by "+userName);
		} else {
			sendRejectDonationEmails(donation.getUsers());
			response.setMessage("Donation rejected by "+userName);
		}
		response.setStatus("Success");
		return response;
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

}
