package com.hariyali.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Plantation;

public interface PlantationService {

	public ByteArrayInputStream exportExcelUserPlant(String seasonType);

	public String uploadPlantationExcel(XSSFWorkbook workbook);

	public List<Plantation> getPlantationsByDonationId(Long donationId);
	
	public Map<String, Object> getAllPlantationMaster(Integer page, Integer size);
}
