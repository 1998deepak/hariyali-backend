package com.hariyali.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantationMasterDTO {
	

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
