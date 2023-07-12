package com.hariyali.dto;

import java.util.Date;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackagesRequest {

	private int packageId;

	private String title;

	private String description;

	private double price;

	private Date createdAt;

	private Date updatedAt;

	private String createdBy;

	private String updatedBy;

	private Boolean isdeleted = Boolean.FALSE;

	private Boolean active = Boolean.FALSE;

}
