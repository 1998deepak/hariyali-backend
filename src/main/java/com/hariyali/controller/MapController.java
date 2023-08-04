//package com.hariyali.controller;
//
//import java.util.List;
//
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.dto.MapRequest;
//import com.hariyali.entity.MapEntity;
//import com.hariyali.exceptions.CustomException;
//import com.hariyali.service.MapService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1")
//public class MapController {
//	
//	@Autowired
//	private MapService mapService;
//	
//	private static final Logger logger = LoggerFactory.getLogger(MapController.class);
//	
//	@PostMapping("/AddMap")
//	public ResponseEntity<ApiResponse<MapRequest>> addMapDetails(@RequestBody MapRequest mapRequest) {
//		
//		return new ResponseEntity<>(this.mapService.saveMap(mapRequest), HttpStatus.OK);
//	}
//
//	
//	
//	
//}
