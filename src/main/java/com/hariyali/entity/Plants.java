package com.hariyali.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_plants")
public class Plants {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plant_id")
	private int plantId;

	@Column(name = "plant_name")
	private String plantName;

	@Column(name = "plant_description")
	private String plantDescription;

	@Column(name = "plant_stock")
	private int plantStock;

	@Column(name = "plant_price")
	private double plantPrice;

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

	@Column(name = "deleted")
	private Boolean isDeleted = Boolean.FALSE;

	// bi-directional many-to-one association to Usertypmst
	@ManyToOne
	@JoinColumn(name = "packageId")
	private Packages packages;

}
