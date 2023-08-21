package com.hariyali.dao.paymentGateway;

import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;

public interface PaymentGatewayConfigurationDao {

    PaymentGatewayConfiguration findByGatewayName(String gatewayName);
}
