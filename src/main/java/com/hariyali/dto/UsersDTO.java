package com.hariyali.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hariyali.entity.Address;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(value = Include.NON_NULL)
public class UsersDTO {

	private int userId;

	private String firstName;

	private String lastName;

	private String mobileNo;

	private String emailId;

	private String donarType;

	private String organisation;

	private Date lastloginDate;

	private Integer attempts;
	
	private String donorId;

	private String webId;

	private String paymentStatus;

	private Date createdDate;

	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;

	private String status;
	
	private String prefix;
	
	private Boolean isTaxBenifit;
	
	private String panCard;
	
	private String password;
	
	private List<AddressDTO> address;

	private List<DonationDTO> donations;

	private Boolean isDeleted;

	private Boolean isApproved;
	
	private Roles userRole;

	private String activityType;
	
	private String citizenship ;
	
	private String country ;

	private String approvalStatus;
	
	private String remark;

	private String source;
	
	private String meconnectId;
}
