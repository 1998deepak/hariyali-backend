package com.hariyali.entity.paymentGateway;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbl_payment_gateway_configuration")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewayConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "gateway_configuration_id")
    Integer id;

    @Column(name = "gateway_name")
    String gatewayName;

    @Column(name = "merchant_Id")
    String merchantId;

    @Column(name = "access_code")
    String accessCode;

    @Column(name = "access_key")
    String accessKey;

    @Column(name = "gateway_url")
    String gatewayURL;

    @Column(name = "redirect_url")
    String redirectURL;

}
