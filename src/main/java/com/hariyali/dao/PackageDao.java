package com.hariyali.dao;

import java.util.List;
import java.util.Map;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PackagesRequest;

public interface PackageDao {

	
	public PackagesRequest savePackage(PackagesRequest packages);
	
	public PackagesRequest updatePackage(PackagesRequest packages,int packageId);
	
	public PackagesRequest getPackageById(int packageId);
	
	public PackagesRequest getPackageByTitle(String packageTtitle);
	
	public PackagesRequest deletePackageById(int packageId);
	
	public Boolean  existsByTitle(String packageTitle);	
	
	List<Map<String, Object>> getAllPackages();
	
	
	
}
