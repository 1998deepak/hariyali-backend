package com.hariyali.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PackagesRequest;


public interface PackagesService {
	
	
	
	public ApiResponse<PackagesRequest> getByPackageTitle(String packageTitle);


	public ApiResponse<PackagesRequest> updatePackage( PackagesRequest pkg, int packageId);

	public ApiResponse<PackagesRequest> deletePackageById(int packageId);

	 public List<Map<String, Object>> getAllPackages();
	 
	 public ApiResponse<String> setActivePackagetoInactive(Date endDate);

	ApiResponse<PackagesRequest> savePackage(JsonNode jsonNode, HttpServletRequest request);

	ApiResponse<PackagesRequest> getByPackageId(String packageId);


}
