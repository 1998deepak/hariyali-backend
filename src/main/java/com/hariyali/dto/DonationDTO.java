package com.hariyali.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hariyali.entity.PaymentInfo;
import com.hariyali.entity.Recipient;
import com.hariyali.entity.UserPackages;

import lombok.Data;

@Data

@JsonInclude(value = Include.NON_NULL)
public class DonationDTO {
	
	private int donationId;
	
	private String donationCode;

	private String donationType;

	private Date donationDate;
	
	private String donationMode;

	private String donationEvent;
	
	private double totalAmount;

	private double generalDonation;
	
	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;
	public String orderId;
	
	private List<PaymentInfo> paymentInfo;

//	private UsersDTO users;

	private List<Recipient> recipient;
		
	private MapDTO donationMap;
		
	private TransactionDTO transactionDonation;
	
	private Boolean isDeleted;
	
	private List<UserPackages> userPackage;

}
