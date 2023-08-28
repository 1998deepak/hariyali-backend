package com.hariyali.serviceimpl;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccavenue.security.AesCryptUtil;
import com.hariyali.EnumConstants;
import com.hariyali.dao.paymentGateway.PaymentGatewayConfigurationDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaymentInfoDTO;
import com.hariyali.entity.Donation;
import com.hariyali.entity.PaymentInfo;
import com.hariyali.entity.Receipt;
import com.hariyali.entity.Users;
import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.PaymentInfoRepository;
import com.hariyali.repository.ReceiptRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.PaymentIntegrationService;
import com.hariyali.service.ReceiptService;
import com.hariyali.utils.EmailService;

/**
 * Implementation class for PaymentIntegrationService interface
 *
 * @author Vinod
 * @version 1.0
 * @date 20/08/2023
 */
@Service
public class PaymentIntegrationServiceImpl implements PaymentIntegrationService {

	@Autowired
	private PaymentGatewayConfigurationDao gatewayConfigurationDao;

	@Autowired
	private DonationRepository donationRepository;

	@Autowired
	private PaymentInfoRepository paymentInfoRepository;

	@Autowired
	UsersRepository userRepository;

	@Autowired
	UsersServiceImpl userService;

	@Autowired
	ReceiptService receiptService;

	@Autowired
	EmailService emailService;

	@Autowired
	ReceiptRepository receiptRepository;


	@Override
	public ApiResponse<String> confirmPayment(String encryptedResponse) {
		// get payment gateway configuration for CCAVENUE
		PaymentGatewayConfiguration gatewayConfiguration = gatewayConfigurationDao.findByGatewayName("CCAVENUE");

		AesCryptUtil aesUtil = new AesCryptUtil(gatewayConfiguration.getAccessKey());
		String decryptedResponse = aesUtil.decrypt(encryptedResponse);

		Map<String, String> response = Arrays.stream(of(decryptedResponse.split("&")).orElse(new String[] {}))
				.filter(values -> !values.isEmpty())
				.collect(Collectors.toMap(s -> ofNullable(s.split("=")).filter(data-> data.length > 0).map(data -> data[0]).orElse(""), s -> ofNullable(s.split("=")).filter(data-> data.length > 1).map(data -> data[0]).orElse("")));

		Donation donation = donationRepository
				.findByOrderId(ofNullable(response.get("order_id")).orElse("0"));
		if (isNull(donation))
			throw new CustomException("Invalid order id received");
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setUserDonation(donation);
		paymentInfo.setAmount(donation.getTotalAmount());
		paymentInfo.setCreatedBy(ofNullable(response.get("billing_email")).orElse(""));
		paymentInfo.setCreatedDate(new Date());
		paymentInfo.setIsDeleted(false);
		paymentInfo.setModifiedBy(ofNullable(response.get("billing_email")).orElse(""));
		paymentInfo.setModifiedDate(new Date());
		paymentInfo.setPaymentDate(new Date());
		paymentInfo.setPaymentMode(ofNullable(response.get("payment_mode")).orElse(""));
		paymentInfo.setPaymentStatus(ofNullable(response.get("order_status")).orElse(""));
		paymentInfo.setRemark(ofNullable(response.get("failure_message")).orElse(""));
		paymentInfo.setPaymentTrackingId(ofNullable(response.get("tracking_id")).orElse(""));
		paymentInfo.setBankPaymentRefNo(ofNullable(response.get("bank_ref_no")).orElse(""));
		paymentInfo.setCardName(ofNullable(response.get("card_name")).orElse(""));
		paymentInfo.setCurrency(ofNullable(response.get("currency")).orElse(""));
		paymentInfo.setOrderId(donation.getOrderId());
		paymentInfo = paymentInfoRepository.save(paymentInfo);
		Users user = userRepository.getUserByDonationId(donation.getDonationId());
		if (user.getWebId() == null) {
			user.setWebId(userService.generateWebId());
			userRepository.save(user);
			System.out.println("user"+user);
			emailService.sendWebIdEmail(user.getEmailId(),user);
		}
		if (paymentInfo.getPaymentStatus().equalsIgnoreCase("Completed")) {
			receiptService.generateReceipt(donation);
			Receipt receipt = receiptRepository.getUserReceiptbyDonation(user.getUserId(), donation.getDonationId());
			try {
				emailService.sendEmailWithAttachment(user.getEmailId(), EnumConstants.subject, EnumConstants.content,
						receipt.getReciept_Path(),user);
			} catch (MessagingException e) {
				throw new CustomException(e.getMessage());
			}
		}
		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setData(paymentInfo.getOrderId());
		return apiResponse;
	}

	@Override
	public ApiResponse<PaymentInfoDTO> findPaymentInfoByOrderId(String orderId) {
		ApiResponse<PaymentInfoDTO> response = new ApiResponse<>();
		PaymentInfo info = paymentInfoRepository.findByOrderId(orderId);
		PaymentInfoDTO dto = new PaymentInfoDTO();
		dto.setBankPaymentRefNo(info.getBankPaymentRefNo());
		dto.setPaymentStatus(info.getPaymentStatus());
		dto.setRemark(info.getRemark());
		response.setData(dto);
		response.setStatus("Success");
		return response;
	}

}
