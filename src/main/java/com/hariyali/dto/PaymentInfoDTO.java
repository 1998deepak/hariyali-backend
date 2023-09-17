package com.hariyali.dto;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;

@Data
public class PaymentInfoDTO {

	private int paymentInfoId;

	private String paymentMode;

	private String bankName;

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

	private String paymentTrackingId;

	private String bankPaymentRefNo;

	private String cardName;

	private String currency;

	private String paymentStatus;

	private String orderId;

	private Integer accountId;

	private Date receiptDate;

	private Double receivedAmount;

	private Double bankCharge;

	private String documentNumber;

	private String bankAddress;

	private String depositNumber;

	private Date depositDate;

	private String receiptNumber;

	private String realizationDate;

	private String creditCardNumber;

	private String cardExpiry;

	private String cardHolderName;

	private String chequeNumber;

	private String chequeDate;

	private String demandDraftNumber;

	private String demandDraftDate;

}
