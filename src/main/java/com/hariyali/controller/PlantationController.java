package com.hariyali.controller;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hariyali.entity.Plantation;
import com.hariyali.repository.PlantationRepository;
import com.hariyali.service.PlantationService;

@RestController
@RequestMapping("/api/v1")
public class PlantationController {

	private static final Logger logger = LoggerFactory.getLogger(PlantationController.class);

	@Autowired
	private PlantationService plantationService;

	@Autowired
	private PlantationRepository plantationRepository;

	@GetMapping("/excelExportUserPlant/{donationType}/{packageName}")
	public void exportExcelUserPlant(HttpServletResponse response,@PathVariable String donationType,@PathVariable String packageName) {
		
		System.err.println(donationType);
		try {
			ByteArrayInputStream byteArrayInputStream = plantationService.exportExcelUserPlant(donationType,packageName);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename= User Plant Report.xlsx");
			IOUtils.copy(byteArrayInputStream, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostMapping("/uploadPlantationExcel")
	public ResponseEntity<?> uploadPlantationExcel(@RequestParam("file") MultipartFile excelfile ) {
		try {
			String uploadRPTRows = "Failed";
			Map<Object, Object> map = new HashMap<>();
			String contentType = excelfile.getContentType();
			String originalFilename = excelfile.getOriginalFilename();
			if (excelfile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
					|| excelfile.getContentType().equals("application/vnd.ms-excel")) {
				XSSFWorkbook workbook = new XSSFWorkbook(excelfile.getInputStream());
				uploadRPTRows = plantationService.uploadPlantationExcel(workbook);

			}
			System.out.println("contentType : " + contentType);
			System.out.println("originalFilename : " + originalFilename);
			map.put("Message", uploadRPTRows);
			return new ResponseEntity<>(map, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	@GetMapping("/plantations")
	public ResponseEntity<List<Plantation>> getPlantationsByDonationId(@RequestParam Long donationId) {
		if (donationId != null) {
			List<Plantation> plantationDTOs = plantationService.getPlantationsByDonationId(donationId);
			return new ResponseEntity<>(plantationDTOs, HttpStatus.OK);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

}
