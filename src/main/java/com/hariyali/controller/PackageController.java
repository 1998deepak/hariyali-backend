package com.hariyali.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiRequest;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PackagesRequest;
import com.hariyali.service.PackagesService;

@RestController
@RequestMapping("/api/v1")
public class PackageController {
	private static final Logger logger = LoggerFactory.getLogger(PackageController.class);

	@Autowired
	private PackagesService packageService;

	// code to get all packages
	@GetMapping("/getAllPackages")
	public ApiResponse<String> getAllPackages() {
		String packages = packageService.getAllPackages();
		ApiResponse<String> response = new ApiResponse<>();
		response.setData(packages);
		response.setStatus(EnumConstants.SUCCESS);
		response.setStatusCode(HttpStatus.OK.value());
		return response;

	}

	// method to get package details by package-Id
	@GetMapping("/package-by-id/{packageId}")
	public ResponseEntity<ApiResponse<PackagesRequest>> getByPackgeId(@PathVariable String packageId) {

		return new ResponseEntity<>(this.packageService.getByPackageId(packageId), HttpStatus.OK);

	}

	// get package details by package title
	@GetMapping("/package-by-title/{title}")
	public ResponseEntity<ApiResponse<PackagesRequest>> getByPackageTitle(@PathVariable String title) {

		return new ResponseEntity<>(this.packageService.getByPackageTitle(title), HttpStatus.OK);
	}

	// method to add package
	@PostMapping("/AddPackage")
	public ResponseEntity<ApiResponse<PackagesRequest>> addPackage(@RequestBody String formData,
			HttpServletRequest request) throws JsonProcessingException {
		ApiRequest response = new ApiRequest(formData);
		return new ResponseEntity<>(this.packageService.savePackage(response.getFormData(), request), HttpStatus.OK);

	}

	// method to update package by package Id
	@PutMapping("/updatPackage/{packageId}")
	public ResponseEntity<ApiResponse<PackagesRequest>> updateUser(@PathVariable String packageId,
			@RequestBody PackagesRequest pkg) {
		return new ResponseEntity<>(this.packageService.updatePackage(pkg, Integer.parseInt(packageId)), HttpStatus.OK);
	}

	// method to delete package by package-title
	@DeleteMapping("/deletePackage/{packageId}")
	public ResponseEntity<ApiResponse<PackagesRequest>> deleteUser(@PathVariable String packageId) {
		return new ResponseEntity<>(this.packageService.deletePackageById(Integer.parseInt(packageId)), HttpStatus.OK);
	}

	@PostMapping("/inactivePackages")
	public ResponseEntity<ApiResponse<String>> activateInactivePackages(@RequestParam("endDate") Date endDate) {
		return new ResponseEntity<>(this.packageService.setActivePackagetoInactive(endDate), HttpStatus.OK);
	}

}
