package com.hariyali.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserPackageDTO {

	private int packageId;

	private String packageName;

	private double bouquetPrice;

	private double maintenanceCost;

	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;

	private Boolean isdeleted = Boolean.FALSE;

	private DonationDTO userDonation;

	private double amount;

	private double totalAmount;

	private Integer noOfBouquets;

	private double generalDonation;

}
