package com.hariyali.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantRequest {
	
	private String title; 
	private double quantity;
	private double total;
	private double cost;
	private boolean status;



}
