package com.hariyali.service;

import java.io.ByteArrayInputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface PlantationService {
	public ByteArrayInputStream exportExcelUserPlant();

	public String uploadPlantationExcel(XSSFWorkbook workbook);

}
