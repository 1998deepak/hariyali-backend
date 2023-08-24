package com.hariyali.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.PaymentInfo;

@Repository
public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Integer> {

	public PaymentInfo findByCreatedBy(String createdBy);
	
	PaymentInfo findByPaymentInfoId(int paymentInfoId);

	@Query(value="select * from tbl_payment_info where donationId=?",nativeQuery=true)
	List<PaymentInfo> findPaymentByDonationId(int donationId);
	
	@Query(value="SELECT payment_status FROM tbl_payment_info where donationId=? Limit 1",nativeQuery = true)
	String getPaymentStatusByDonationId(int donationId);

	public PaymentInfo findByOrderId(String orderId);
	
	
}
