package com.hariyali.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_transaction")
@Data
//@FilterDef(name = "deletedQuestionFilter", parameters = @ParamDef(name = "isDeleted",type = Boolean.class))
@Filter(name = "deletedQuestionFilter", condition = "deleted = :isDeleted")


public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private int transactionId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "transaction_date")
	private Date transactionDate;

	@Column(name = "amount")
	private double amount;



	@Column(name = "status")
	private String transactionStatus;

	// bi-directional many-to-one association to Usertypmst
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users userTransaction;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "modified_by")
	private String modifiedBy;

	

	
	@OneToOne
	@JoinColumn(name = "donation_id")
	private Donation donationTransaction;

}
