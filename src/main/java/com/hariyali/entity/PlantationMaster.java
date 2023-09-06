package com.hariyali.entity;

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
@Table(name = "_tbl_plantation_master")
public class PlantationMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String state;
	private String district;
	private String village;
	private String season;
	private String plot;
	private Long noOfPlantsPlanted;
	private String plantationDate;
	private Float lattitude;
	private Float longitude;
	private String status;

}
