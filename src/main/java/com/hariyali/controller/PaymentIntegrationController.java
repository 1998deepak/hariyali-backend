package com.hariyali.controller;

import com.ccavenue.security.AesCryptUtil;
import com.hariyali.dao.paymentGateway.PaymentGatewayConfigurationDao;
import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import com.hariyali.service.PaymentIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller to handle payment gateway integration
 *
 * @Author Vinod
 * @version 1.0
 * @date 20/08/2023
 */
@Controller
public class PaymentIntegrationController {


    @Value("${frontend.redirect-url}")
    String frontendRedirectURL;

    @Autowired
    PaymentIntegrationService service;

    @RequestMapping(value = "/page/paymentIntegration", method = RequestMethod.POST)
    public void paymentIntegration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String encryptedResponse = request.getParameter("encResp");
        response.sendRedirect(frontendRedirectURL + service.confirmPayment(encryptedResponse).getData());
    }
}
