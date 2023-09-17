package com.hariyali.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_plantation_master")
public class PlantationMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	private String state;
	private String district;
	private String village;
	private String season;
	private String plot;
	private Long noOfPlantsPlanted;
	private Date plantationDate;
	private Double latitude;
	private Double longitude;
	private String status;
	@Column(name = "created_date")
	private Date created_date;
	@Column(name = "created_by")
	private String created_by;
}
