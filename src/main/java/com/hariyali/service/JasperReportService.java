package com.hariyali.service;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;

import com.hariyali.dto.ApiResponse;

import net.sf.jasperreports.engine.JRException;

public interface JasperReportService {

	 ApiResponse<String> generatePdf(String reciptNo) throws FileNotFoundException, JRException;
	
	 ApiResponse<String> generateCertificate(HttpServletRequest request,String certiNo)throws FileNotFoundException, JRException;
	
}
