package com.hariyali.dto;

import java.time.LocalDate;
import java.util.List;

import com.hariyali.entity.Commitment;
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
	
	private long id;
	
	private long userId;
	
	private long donationId;
	
	private long packageId;
	
	private String username;
	
	private String packageName;
	
	private float amount;
	
	private String plantName;
	
	private long quantity;
	
	private LocalDate plantDate;
	
	private String plantLocation;
	
	private UserPackages userPackages;
	
	private List<Commitment> commitment;

}
