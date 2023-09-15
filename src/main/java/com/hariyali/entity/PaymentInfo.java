package com.hariyali.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_payment_info")
public class PaymentInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "paymentInfo_id")
	private int paymentInfoId;

	@Column(name = "payment_mode")
	private String paymentMode;

	@Column(name = "bank_name")
	private String bankName;

	
	@Column(name = "chq_OR_dd_no")
	private String chqORddNo;

	@Column(name = "chq_OR_dd_date")
	@Temporal(TemporalType.DATE)
	private Date chqORddDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "amount")
	private double amount;

	@Column(name ="payment_status")
	private String paymentStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "modified_by")
	private String modifiedBy;
	
	@Column(name = "remark")
	private String remark;

	@Column(name = "is_deleted")
	private Boolean isDeleted = Boolean.FALSE;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "donationId", referencedColumnName = "donation_id")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "donationId")
	private Donation userDonation;

	@Column(name = "payment_tracking_id")
	private String paymentTrackingId;

	@Column(name = "bank_payment_ref_no")
	private String bankPaymentRefNo;

	@Column(name = "card_name")
	private String cardName;

	@Column(name = "currency")
	private String currency;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "account_id")
	private Integer accountId;

	@Column(name = "receipt_date")
	private Date receiptDate;

	@Column(name = "received_amount")
	private Double receivedAmount;

	@Column(name = "bank_charge")
	private Double bankCharge;

	@Column(name = "document_number")
	private String documentNumber;

	@Column(name = "bank_address")
	private String bankAddress;

	@Column(name = "deposit_number")
	private String depositNumber;

	@Column(name = "deposit_date")
	private Date depositDate;

	@Column(name = "receipt_number")
	private String receiptNumber;

	@Column(name = "realization_date")
	private String realizationDate;

	@Column(name = "credit_card_no")
	private String creditCardNumber;

	@Column(name = "expiry")
	private String cardExpiry;

	@Column(name = "cardholder_name")
	private String cardHolderName;

	@Column(name = "cheque_number")
	private String chequeNumber;

	@Column(name = "cheque_date")
	private String chequeDate;

	@Column(name = "dd_number")
	private String demandDraftNumber;

	@Column(name = "dd_date")
	private String demandDraftDate;
	
	@Column(name = "source_type")
	private String sourceType;

}
