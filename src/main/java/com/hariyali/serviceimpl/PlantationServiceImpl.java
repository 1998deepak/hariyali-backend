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
import org.apache.poi.xssf.usermodel.XSSFCell;
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
import com.hariyali.entity.Donation;
import com.hariyali.entity.Plantation;
import com.hariyali.entity.Recipient;
import com.hariyali.entity.UserPackages;
import com.hariyali.entity.Users;
import com.hariyali.repository.CommitmentRepository;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.PlantationRepository;
import com.hariyali.repository.RecipientRepository;
import com.hariyali.repository.UserPackageRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.PlantationService;
import com.hariyali.utils.EmailService;

@Service
public class PlantationServiceImpl implements PlantationService {

	@Autowired
	private UsersRepository usersRepository;
	private static ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private PlantationRepository plantationRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private DonationRepository donationRepository;
	
	@Autowired
	private UserPackageRepository userPackageRepository;

	@Autowired
	private CommitmentRepository commitmentRepository;
	
	@Autowired
	private RecipientRepository recipientRepository;
	

	@Override
	public ByteArrayInputStream exportExcelUserPlant() {
		
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
			cell.setCellValue("State");
			sheet.autoSizeColumn(0);
			cell.setCellStyle(style);

			cell = row.createCell(1);
			cell.setCellValue("District");
			sheet.autoSizeColumn(1);
			cell.setCellStyle(style);

			cell = row.createCell(2);
			cell.setCellValue("Village");
			sheet.autoSizeColumn(2);
			cell.setCellStyle(style);

			cell = row.createCell(3);
			cell.setCellValue("Season");
			sheet.autoSizeColumn(3);
			cell.setCellStyle(style);

			cell = row.createCell(4);
			cell.setCellValue("Plot");
			sheet.autoSizeColumn(4);
			cell.setCellStyle(style);

			cell = row.createCell(5);
			cell.setCellValue("NoOfPlantsPlanted ");
			sheet.autoSizeColumn(5);
			cell.setCellStyle(style);

			cell = row.createCell(6);
			cell.setCellValue("Plantation Date");
			sheet.autoSizeColumn(6);
			cell.setCellStyle(style);

			cell = row.createCell(7);
			cell.setCellValue("Lattitude");
			sheet.autoSizeColumn(7);
			cell.setCellStyle(style);
			
			cell = row.createCell(8);
			cell.setCellValue("Longitude");
			sheet.autoSizeColumn(8);
			cell.setCellStyle(style);
			
			cell = row.createCell(9);
			cell.setCellValue("Status");
			sheet.autoSizeColumn(9);
			cell.setCellStyle(style);
			
			
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
	        // ======================== Read Data From excel =================================================
	        XSSFSheet worksheet = workbook.getSheetAt(0);
	        System.out.println("From Xls file Physical rows:=================:" + worksheet.getPhysicalNumberOfRows());
	        System.out.println("Last Row: " + worksheet.getLastRowNum());
	        int i = 1;

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

	        List<Commitment> commitments = new ArrayList<>();
	        List<Plantation> plantations = new ArrayList<>();

	        while (i <= worksheet.getLastRowNum()) {
	            XSSFRow row = worksheet.getRow(i++);

	            // Check if the row itself is null (empty row)
	            if (row == null) {
	                System.out.println("Empty row found at row " + (i - 1) + ". Skipping.");
	                continue; // Skip this row and move to the next
	            }

	            XSSFCell plantNameCell;
	            XSSFCell quantityCell;
	            XSSFCell plantDateCell;
	            XSSFCell plantLocationCell;
	            XSSFCell startDateCell;
	            XSSFCell recipientIdCell;
	            XSSFCell donationTypeCell = row.getCell(7);
	            
	            String donationType = donationTypeCell.toString().trim();
	            long recipentId = 0;

	            
	            if(donationType.equals("Self-Donate"))
	            {
	            	// Get the values from the Excel cells
	            	plantNameCell = row.getCell(8);
		           	quantityCell = row.getCell(9);
		           	plantDateCell = row.getCell(10);
		        	plantLocationCell = row.getCell(11);
		        	startDateCell = row.getCell(12);

	            }else {
	            	// Get the values from the Excel cells
	            	recipientIdCell = row.getCell(8);
	            	recipentId=(long) recipientIdCell.getNumericCellValue();
		            plantNameCell = row.getCell(10);
		            quantityCell = row.getCell(11);
		            plantDateCell = row.getCell(12);
		            plantLocationCell = row.getCell(13);
		            startDateCell = row.getCell(14);
	            }
	            
	            // Check if any of the required cells are null
	            if (plantNameCell == null || quantityCell == null || plantDateCell == null || plantLocationCell == null || startDateCell == null) {
	                System.out.println("Validation failed for row " + (i - 1) + ": Some required cells are missing.");
	                continue; // Skip this row and move to the next
	            }

	            // Get the values from the non-null cells
	            String plantName = plantNameCell.toString().trim();
	            String quantityStr = quantityCell.toString().trim();
	            String plantDateStr = plantDateCell.toString().trim();
	            String plantLocation = plantLocationCell.toString().trim();
	            String startDateStr = startDateCell.toString().trim();

	            // Check if any of the required fields are empty
	            if (plantName.isEmpty() || quantityStr.isEmpty() || plantDateStr.isEmpty() || plantLocation.isEmpty() || startDateStr.isEmpty()) {
	                System.out.println("Validation failed for row " + (i - 1) + ": Some required fields are empty.");
	                continue; // Skip this row and move to the next
	            }

	            // Parse the values as needed
	            long user = Integer.parseInt(String.valueOf(row.getCell(0).toString().trim()).split("\\.")[0]);
	            long donation = Integer.parseInt(String.valueOf(row.getCell(1).toString().trim()).split("\\.")[0]);
	            long packages = Integer.parseInt(String.valueOf(row.getCell(2).toString().trim()).split("\\.")[0]);
	            String userName = row.getCell(3).toString().trim();
	            String packagesName = row.getCell(4).toString().trim();
	            float amount = Float.parseFloat(row.getCell(5).toString().trim());

	            long quantity = Integer.parseInt(quantityStr.split("\\.")[0]);
	            String plantDate = plantDateStr;
	            LocalDate formattedPlantationDate = LocalDate.parse(plantDate, formatter);
	            String plantLo = plantLocation;
	            String startDate = startDateStr;
	            LocalDate formattedStartDate = LocalDate.parse(startDate, formatter);

	            UserPackages userPackages = userPackageRepository.getById((int) packages);

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
	            plantations.add(plantation);
	            
				Plantation savedPlantationData = plantationRepository.save(plantation);

	            Plantation plantationId = plantationRepository.getById(savedPlantationData.getId());
	            
	            Users userData = usersRepository.getById((int) savedPlantationData.getUserId());
	            
	            userData.setIsPlanted(true);
	            usersRepository.save(userData);
	            
	            LocalDate endDate = formattedStartDate.plusYears(3);

				// Set properties for Commitment
				Commitment commitment = new Commitment();

				commitment.setPlantation(plantationId);
				commitment.setStartDate(formattedStartDate);
				commitment.setEndDate(endDate);
				commitment.setDateOFPlantation(formattedPlantationDate);

				commitments.add(commitment);
				
				if(donationType.equals("Self-Donate"))
				{
					
					String toMail = userData.getEmailId();
					System.err.println(toMail);
					String subject = "Update about plantation";
					String bodyMessage = "your plantation is done \n and plantation commited start date is "+commitment.getStartDate()+" and end date is "+commitment.getEndDate();
					emailService.sendPlantationEmail(toMail, subject, bodyMessage);
				}else {
					
					Recipient recipent = recipientRepository.getById((int) recipentId);
					
					String toMail = recipent.getEmailId();
					System.err.println("toMail gift"+toMail);
					String subject = "Update about plantation";
					String bodyMessage = "your plantation is done \n and plantation commited start date is "+commitment.getStartDate()+" and end date is "+commitment.getEndDate();
					emailService.sendPlantationEmail(toMail, subject, bodyMessage);
					
				}
				
			

			}
			commitmentRepository.saveAll(commitments);
			
//			plantations.forEach((plant)->{
//				plant.getUserId()
//			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Success";
	}

	@Override
	public List<Plantation> getPlantationsByDonationId(Long donationId) {
		
		return plantationRepository.getPlantationByDonationId(donationId);
	}
}

