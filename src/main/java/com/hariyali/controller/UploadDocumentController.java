package com.hariyali.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hariyali.service.UploadFormTenBeService;

@RestController
@RequestMapping("/api/v1")
public class UploadDocumentController {

	@Autowired
	private UploadFormTenBeService formTenbService;

	@PostMapping("/uploadZipFile")
	public ResponseEntity<?> uploadExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("zipFile") MultipartFile multipartFile) throws IOException {
		Map<String, Object> map = new HashMap();
		if (multipartFile.isEmpty()) {
			map.put("message", "Please select a ZIP file to upload...!");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}
		formTenbService.uploadFormTenBe(multipartFile,request);
		map.put("message", "File uploaded Succcessfully..!");
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

}
