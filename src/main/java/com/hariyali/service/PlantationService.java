package com.hariyali.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hariyali.entity.Plantation;

public interface PlantationService {

	public ByteArrayInputStream exportExcelUserPlant();

	public String uploadPlantationExcel(XSSFWorkbook workbook);

	public List<Plantation> getPlantationsByDonationId(Long donationId);
}
