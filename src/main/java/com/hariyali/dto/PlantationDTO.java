package com.hariyali.dto;

import com.hariyali.entity.Donation;
import com.hariyali.entity.PlantationMaster;
import com.hariyali.entity.UserPackages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantationDTO {

	private Long id;
	private PlantationMasterDTO plantationMaster;
	private Donation donation;
	private UserPackages userPackages;
	private Integer noOfPlantsPlanted;
	private Boolean year1Report;
	private Boolean year2Report;
	private String createdBy;
	private String donorId;
}
