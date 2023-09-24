package com.hariyali.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_state")
public class State {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_date")
	private Date createdDate;
	
	@Column(name="is_active")
	private Boolean isActive;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="updated_date")
	private Date updatedDate;
	
	@Column(name="country_code")
	private String countryCode;
	
	@Column(name="country_name")
	private String countryName;

	@Column(name="country_id")
	private Long countryId;
	
	@Column(name="state_code")
	private String stateCode;
	
	@Column(name="state_name")
	private String stateName;
}