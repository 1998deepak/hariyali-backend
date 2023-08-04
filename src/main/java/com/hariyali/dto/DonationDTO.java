package com.hariyali.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data

@JsonInclude(value = Include.NON_NULL)
public class DonationDTO {
	
	private int donationId;

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
	
	private List<PaymentInfoDTO> paymentInfo;

	private UsersDTO users;

	private List<RecipientDTO> recipient;
		
	private MapDTO donationMap;
		
	private TransactionDTO transactionDonation;
	
	private Boolean isDeleted;
	
	private List<UserPackageDTO> userPackage;

}
