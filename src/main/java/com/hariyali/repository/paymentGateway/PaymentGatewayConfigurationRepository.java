package com.hariyali.repository.paymentGateway;

import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentGatewayConfigurationRepository extends JpaRepository<PaymentGatewayConfiguration, Integer> {

    PaymentGatewayConfiguration findByGatewayName(String gatewayName);
}
