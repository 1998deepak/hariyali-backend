package com.hariyali.controller;

import com.hariyali.service.PackagesService;
import com.hariyali.service.PlantsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PlantController {
	
private static final Logger logger = LoggerFactory.getLogger(PackageController.class);
	
	@Autowired
	private PlantsService plantService;
	
	@Autowired
	private PackagesService packageService;
	
	
	
}
