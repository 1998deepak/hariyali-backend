package com.hariyali.service;

import java.util.Map;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PlantRequest;
import com.hariyali.entity.Plants;

public interface PlantsService {
	public ApiResponse<Map<String, Object>> getAllPlant(int pageNo, int pageSize);

	public ApiResponse<PlantRequest> getByPlantId(int packageId);
	
	public ApiResponse<PlantRequest> getByPlantTitle(String plantTitle);

	public ApiResponse<PlantRequest> savePlant(PlantRequest plant);

	public ApiResponse<Plants> updateplant( PlantRequest plant, int plantId);

	public ApiResponse<Plants> deleteplant(int plantId);
}
