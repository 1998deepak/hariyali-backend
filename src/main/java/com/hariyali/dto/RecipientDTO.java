package com.hariyali.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipientDTO {

	private int recipientId;

	private String firstName;

	private String lastName;

	private String mobileNo;

	private String emailID;
	
	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;

	private List<AddressDTO> address;

	private DonationDTO userDonation;

	private Boolean isDeleted;

}
