package com.hariyali.daoimpl.paymentGateway;

import com.hariyali.dao.paymentGateway.PaymentGatewayConfigurationDao;
import com.hariyali.entity.paymentGateway.PaymentGatewayConfiguration;
import com.hariyali.repository.paymentGateway.PaymentGatewayConfigurationRepository;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentGatewayConfigurationDaoImpl implements PaymentGatewayConfigurationDao {

    @Autowired
    private PaymentGatewayConfigurationRepository repository;

    @Override
    public PaymentGatewayConfiguration findByGatewayName(String gatewayName) {
        return repository.findByGatewayName(gatewayName);
    }

}
