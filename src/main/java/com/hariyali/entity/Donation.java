package com.hariyali.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_donation")
public class Donation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "donation_id")
	private int donationId;

	@Column(name ="donation_code")
	private String donationCode;

	@Column(name = "donation_type")
	private String donationType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "donation_date")
	private Date donationDate;

	@Column(name = "donation_mode")
	private String donationMode;

	@Column(name = "donation_event")
	private String donationEvent;



	@Column(name = "total_amount")
	private double totalAmount;

	@Column(name = "general_donation")
	private double generalDonation;

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

	@JsonIdentityReference(alwaysAsId = true)
	@OneToMany(mappedBy = "userDonation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PaymentInfo> paymentInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", referencedColumnName = "user_id")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	private Users users;

	@OneToMany(mappedBy = "userDonation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIdentityReference(alwaysAsId = true)
	private List<Recipient> recipient;

	@OneToMany(mappedBy = "userDonation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIdentityReference(alwaysAsId = true)
	private List<UserPackages> userPackage;

	@Column(name = "deleted")
	private Boolean isDeleted = Boolean.FALSE;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "source")
	private String source;

	@Column(name = "meconnect_id")
	private Integer meconnectId;

	@Column(name = "approval_status")
	private String approvalStatus;

	@Column(name = "remark")
	private String remark;

	@Column(name = "is_approved")
	private Boolean isApproved = Boolean.FALSE;

	@Column(name = "approval_date")
	private Date approvalDate;

}
