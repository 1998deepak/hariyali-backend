package com.hariyali.entity;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_plantation")
public class Plantation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plantation_id")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "plantation_master_id", referencedColumnName = "id")
	PlantationMaster plantationMaster;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "donation_id", referencedColumnName = "donation_id")
	Donation donation;

	@ManyToOne
	@JoinColumn(name = "user_package_id", referencedColumnName = "package_id")
	private UserPackages userPackages;

	@Column(name = "no_Of_Plants_Planted")
	Integer noOfPlantsPlanted;

	@Column(name = "year1_report")
	Boolean year1Report;

	@Column(name = "year2_report")
	Boolean year2Report;
	
	@Column(name = "year1_report_date")
	Date year1ReportDate;

	@Column(name = "year1_report_By")
	String year1ReportBy;

	@Column(name = "year2_report_date")
	Date year2ReportDate;

	@Column(name = "year2_report_By")
	String year2ReportBy;

	@Column(name = "created_by")
	String createdBy;

}
