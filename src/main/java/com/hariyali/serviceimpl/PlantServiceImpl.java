package com.hariyali.serviceimpl;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PlantRequest;
import com.hariyali.entity.Packages;
import com.hariyali.entity.Plants;
import com.hariyali.repository.PlantRepository;
import com.hariyali.service.PlantsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class PlantServiceImpl {
	
	@Autowired
	private PlantRepository plantRepo;

	
}
