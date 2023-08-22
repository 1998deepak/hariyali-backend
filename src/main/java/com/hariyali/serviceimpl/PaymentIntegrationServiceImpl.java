package com.hariyali.serviceimpl;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccavenue.security.AesCryptUtil;
import com.hariyali.dao.paymentGateway.PaymentGatewayConfigurationDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaymentInfoDTO;
import com.hariyali.entity.Donation;
import com.hariyali.entity.PaymentInfo;
import com.hariyali.entity.Users;
import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.PaymentInfoRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.PaymentIntegrationService;

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
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<Integer> confirmPayment(String encryptedResponse) {
        // get payment gateway configuration for CCAVENUE
        PaymentGatewayConfiguration gatewayConfiguration = gatewayConfigurationDao.findByGatewayName("CCAVENUE");

        AesCryptUtil aesUtil = new AesCryptUtil(gatewayConfiguration.getAccessKey());
        String decryptedResponse = aesUtil.decrypt(encryptedResponse);

        Map<String, String> response = Arrays.stream(of(decryptedResponse.split("&"))
                .orElse(new String[]{}))
                .filter(values -> !values.isEmpty())
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));

        Donation donation = donationRepository.findByDonationId(Integer.parseInt(ofNullable(response.get("order_id")).orElse("0")));
        if (isNull(donation)) throw new CustomException("Invalid order id received");
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
        paymentInfo = paymentInfoRepository.save(paymentInfo);
        Users user=userRepository.getUserByDonationId(donation.getDonationId());
        user.setWebId(userService.generateWebId());
        ApiResponse<Integer> apiResponse = new ApiResponse<>();
        apiResponse.setData(paymentInfo.getPaymentInfoId());
        return apiResponse;
    }

    @Override
    public ApiResponse<PaymentInfoDTO> findPaymentInfoByPaymentInfoId(Integer paymentInfoId) {
        ApiResponse<PaymentInfoDTO> response = new ApiResponse<>();
        PaymentInfo info = paymentInfoRepository.findByPaymentInfoId(paymentInfoId);
        PaymentInfoDTO dto = new PaymentInfoDTO();
        dto.setBankPaymentRefNo(info.getBankPaymentRefNo());
        dto.setPaymentStatus(info.getPaymentStatus());
        dto.setRemark(info.getRemark());
        response.setData(dto);
        response.setStatus("Success");
        return response;
    }

}
