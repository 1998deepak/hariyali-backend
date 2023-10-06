package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaymentInfoDTO;

import javax.servlet.http.HttpSession;

/**
 * Interface to handle payment integration confirmation
 *
 * @author Vinod
 * @version 1.0
 * @date 20/08/2023
 */
public interface PaymentIntegrationService {

    ApiResponse<String> confirmPayment(String encryptedResponse, HttpSession session);

    ApiResponse<PaymentInfoDTO> findPaymentInfoByOrderId(String orderId);
}
