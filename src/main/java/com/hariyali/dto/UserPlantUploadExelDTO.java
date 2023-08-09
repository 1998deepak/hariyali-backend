package com.hariyali.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPlantUploadExelDTO {
	private long user;
	private long donation;
	private long packages;
	private float amount;
	private String userName;
	private String packageName;
	
}
