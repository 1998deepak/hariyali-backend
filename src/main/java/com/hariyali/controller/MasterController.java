package com.hariyali.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hariyali.entity.Citzenship;
import com.hariyali.entity.Country;
import com.hariyali.entity.State;
import com.hariyali.service.MasterService;

@RestController
@RequestMapping("/api/v1")
public class MasterController {
	
	@Autowired
	private MasterService masterService;
	
	@GetMapping("/getAllCountry")
	public List<Country> getAllCountry() {
		
		return masterService.getAllCountry();
	}
	@GetMapping("/getAllStateByCountryId/{countryId}")
	public List<State> getAllStateByCountryId(@PathVariable long countryId) {
		
		return masterService.getAllStateByCountryId(countryId);
	}
	@GetMapping("/getAllCitizensip")
	public List<Citzenship> getAllCitizensip() {
		
		return masterService.getAllCitizensip();
	}

}
