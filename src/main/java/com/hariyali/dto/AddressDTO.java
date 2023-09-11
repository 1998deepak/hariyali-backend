package com.hariyali.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class AddressDTO {

	private int addressId;

	private String street1;

	private String street2;

	private String street3;

	private String country;

	private String state;

	private String city;

	private String postalCode;

	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;

//	private UsersDTO users;
//
	private RecipientDTO recipient;

	private Boolean isDeleted;

}
