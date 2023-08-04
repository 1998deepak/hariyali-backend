package com.hariyali.dto;

import java.util.Date;

import com.hariyali.entity.Donation;
import com.hariyali.entity.Users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {

	private int myTransactionId;

	private int transactionId;

	private Date transactionDate;

	private double amount;

	private String transactionStatus;

	private Users userTransaction;

	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;

	private Donation donationTransaction;

}
