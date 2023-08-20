package com.hariyali.serviceimpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.dto.UserPlantUploadExelDTO;
import com.hariyali.entity.Commitment;
import com.hariyali.entity.Plantation;
import com.hariyali.entity.UserPackages;
import com.hariyali.repository.CommitmentRepository;
import com.hariyali.repository.PlantationRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.PlantationService;

@Service
public class PlantationServiceImpl implements PlantationService {

	@Autowired
	private UsersRepository usersRepository;
	private static ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private PlantationRepository plantationRepository;
	
	@Autowired
	private UserPackageRepository userPackageRepository;
	
	@Autowired
	private CommitmentRepository commitmentRepository;

	@Override
	public ByteArrayInputStream exportExcelUserPlant() {

		List<UserPlantUploadExelDTO> userPlantUploadExelDTOs = mapper.convertValue(
				usersRepository.getUserPlantExportExcel(), new TypeReference<List<UserPlantUploadExelDTO>>() {
				});
		
		Workbook workbook = new SXSSFWorkbook();
		try {
			
			Sheet sheet = workbook.createSheet("User Plant Report ");
			
			CellStyle cellStyle1 = workbook.createCellStyle();
			
			Row row = sheet.createRow(0);

			CellStyle style = workbook.createCellStyle();
			
			XSSFFont font = (XSSFFont) workbook.createFont();
			
			font.setBold(true);
			font.setFontHeight(12);
			style.setFont(font);

			Cell cell = row.createCell(0);
			cell.setCellValue("User");
			sheet.autoSizeColumn(0);
			cell.setCellStyle(style);

			cell = row.createCell(1);
			cell.setCellValue("Donation");
			sheet.autoSizeColumn(1);
			cell.setCellStyle(style);

			cell = row.createCell(2);
			cell.setCellValue("Package");
			sheet.autoSizeColumn(2);
			cell.setCellStyle(style);

			cell = row.createCell(3);
			cell.setCellValue("User name");
			sheet.autoSizeColumn(3);
			cell.setCellStyle(style);

			cell = row.createCell(4);
			cell.setCellValue("Package name");
			sheet.autoSizeColumn(4);
			cell.setCellStyle(style);

			cell = row.createCell(5);
			cell.setCellValue("Donation amt");
			sheet.autoSizeColumn(5);
			cell.setCellStyle(style);

			cell = row.createCell(6);
			cell.setCellValue("Plant Name");
			sheet.autoSizeColumn(6);
			cell.setCellStyle(style);

			cell = row.createCell(7);
			cell.setCellValue("Quantity");
			sheet.autoSizeColumn(7);
			cell.setCellStyle(style);

			cell = row.createCell(8);
			cell.setCellValue("Plant Date");
			sheet.autoSizeColumn(8);
			cell.setCellStyle(style);

			cell = row.createCell(9);
			cell.setCellValue("Plant Location");
			sheet.autoSizeColumn(9);
			cell.setCellStyle(style);
			
			cell = row.createCell(10);
			cell.setCellValue("Start Date");
			sheet.autoSizeColumn(10);
			cell.setCellStyle(style);
		
			
			int rowCount = 1;
			
			for (UserPlantUploadExelDTO dto : userPlantUploadExelDTOs) {
				Row rowdata = sheet.createRow(rowCount++);
				rowdata.createCell(0).setCellValue(dto.getUser());
				rowdata.createCell(1).setCellValue(dto.getDonation());
				rowdata.createCell(2).setCellValue(dto.getPackages());
				rowdata.createCell(3).setCellValue(dto.getUserName());
				rowdata.createCell(4).setCellValue(dto.getPackageName());
				rowdata.createCell(5).setCellValue(dto.getAmount());
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public String uploadPlantationExcel(XSSFWorkbook workbook) {
		int countRow = 0;
		System.out.println("Total worksheet:-----------" + workbook.getNumberOfSheets());
		try {
//		======================== Read Data From excel =================================================
			XSSFSheet worksheet = workbook.getSheetAt(0);
			System.out.println("From Xls file Physical rows:=================:" + worksheet.getPhysicalNumberOfRows());
			System.out.println("Last Row: " + worksheet.getLastRowNum());
			int i = 1;
			
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

			List<Commitment> commitments = new ArrayList<>();
			
			while (i <= worksheet.getLastRowNum()) {
				XSSFRow row = worksheet.getRow(i++);
				long user = Integer.parseInt(String.valueOf(row.getCell(0).toString().trim()).split("\\.")[0]);
				long donation = Integer.parseInt(String.valueOf(row.getCell(1).toString().trim()).split("\\.")[0]);
				long packages = Integer.parseInt(String.valueOf(row.getCell(2).toString().trim()).split("\\.")[0]);
				String userName = row.getCell(3).toString().trim();
				String packagesName = row.getCell(4).toString().trim();
				float amount = Float.parseFloat(row.getCell(5).toString().trim());
				String plantName = row.getCell(6).toString().trim();
				long quantity = Integer.parseInt(String.valueOf(row.getCell(7).toString().trim()).split("\\.")[0]);
				String plantDate = row.getCell(8).toString().trim();
		        LocalDate formattedPlantationDate = LocalDate.parse(plantDate, formatter);

				String plantLocation = row.getCell(9).toString().trim();
				
				String startDate = row.getCell(10).toString().trim();
				
				UserPackages userPackages= userPackageRepository.getById((int)packages);
				
				Plantation plantation = new Plantation();
				plantation.setUserId(user);
				plantation.setDonationId(donation);
				plantation.setPackageId(packages);
				plantation.setUsername(userName);
				plantation.setAmount(amount);
				plantation.setPackageName(packagesName);
				plantation.setQuantity(quantity);
				plantation.setPlantDate(formattedPlantationDate);
				plantation.setUserPackages(userPackages);
				plantation.setPlantName(plantName);
				plantation.setPlantLocation(plantLocation);
				
				
				Plantation savedPlantationData = plantationRepository.save(plantation);
				
				Plantation plantationId = plantationRepository.getById(savedPlantationData.getId()); 
                
		        LocalDate formattedStartDate = LocalDate.parse(startDate, formatter);
		        
		        LocalDate endDate = formattedStartDate.plusYears(3);
		        
		        // Set properties for Commitment
                Commitment commitment = new Commitment();
                
                commitment.setPlantation(plantationId);
                commitment.setStartDate(formattedStartDate);
                commitment.setEndDate(endDate);
                commitment.setDateOFPlantation(formattedPlantationDate);
                
                commitments.add(commitment);	        
				
			}
			commitmentRepository.saveAll(commitments);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Success";
	}
	
	
	

}
