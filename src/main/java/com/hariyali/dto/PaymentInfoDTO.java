package com.hariyali.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PaymentInfoDTO {

	private int paymentInfoId;

	private String paymentMode;

	private String bankname;

	private String chqORddNo;

	private Date chqORddDate;

	private Date paymentDate;

	private double amount;
	
	private DonationDTO donation;
	
	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;
	
	private String remark;
	
	private Boolean isDeleted;
}
