package com.hariyali.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Plantation;

public interface PlantationService {

	public ByteArrayInputStream exportExcelUserPlant(String seasonType);

	public String uploadPlantationExcel(XSSFWorkbook workbook);

	public List<Plantation> getPlantationsByDonationId(Long donationId);

	public ApiResponse<Object> getAllPlantationMaster();
}
