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
	private int meconnectId;

	public int getDonationId() {
		return donationId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getMeconnectId() {
		return meconnectId;
	}

	public void setMeconnectId(int meconnectId) {
		this.meconnectId = meconnectId;
	}

	public void setDonationId(int donationId) {
		this.donationId = donationId;
	}

	public String getDonationType() {
		return donationType;
	}

	public void setDonationType(String donationType) {
		this.donationType = donationType;
	}

	public Date getDonationDate() {
		return donationDate;
	}

	public void setDonationDate(Date donationDate) {
		this.donationDate = donationDate;
	}

	public String getDonationMode() {
		return donationMode;
	}

	public void setDonationMode(String donationMode) {
		this.donationMode = donationMode;
	}

	public String getDonationEvent() {
		return donationEvent;
	}

	public void setDonationEvent(String donationEvent) {
		this.donationEvent = donationEvent;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getGeneralDonation() {
		return generalDonation;
	}

	public void setGeneralDonation(double generalDonation) {
		this.generalDonation = generalDonation;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public List<Recipient> getRecipient() {
		return recipient;
	}

	public void setRecipient(List<Recipient> recipient) {
		this.recipient = recipient;
	}

	public List<UserPackages> getUserPackage() {
		return userPackage;
	}

	public void setUserPackage(List<UserPackages> userPackage) {
		this.userPackage = userPackage;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<PaymentInfo> getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(List<PaymentInfo> paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getDonationCode() {
		return donationCode;
	}

	public void setDonationCode(String donationCode) {
		this.donationCode = donationCode;
	}
}
