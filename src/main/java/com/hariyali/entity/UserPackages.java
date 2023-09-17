package com.hariyali.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "tbl_user_packages")
public class UserPackages implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "package_id")
	private int packageId;

	@Column(name = "package_name")
	private String packageName;

	@Column(name = "bouquet_price")
	private double bouquetPrice;

	@Column(name = "maintenance_cost")
	private double maintenanceCost;

	@Column(name = "no_of_bouquets")
	private Integer noOfBouquets;

	@Column(name = "amount")
	private double amount;
	
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

	@Column(name = "deleted")
	private Boolean isdeleted = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "donationId",referencedColumnName = "donation_id")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "donationId")
	private Donation userDonation;
	
	@JsonIgnore
	@OneToMany(mappedBy="userPackages")
	private List<Plantation> plantation;

	private boolean isPlanted;

	@Column(name = "plant_allocated")
	private Integer plantAllocated;
	
}
