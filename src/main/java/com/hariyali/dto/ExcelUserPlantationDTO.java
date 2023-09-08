package com.hariyali.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelUserPlantationDTO {
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
