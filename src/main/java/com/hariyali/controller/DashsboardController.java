//package com.hariyali.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.hariyali.dto.JwtResponse;
//import com.hariyali.service.DashboardService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import jakarta.servlet.http.HttpServletRequest;
//
//@RestController
//@RequestMapping("/api/v1/")
//public class DashsboardController {
//	@Autowired
//	private DashboardService dashboardService;
//
//	private static final Logger logger = LoggerFactory.getLogger(DashsboardController.class);
//
//	
//	@GetMapping("getdashboard")
//	public ResponseEntity<?> getDashboard(HttpServletRequest request){
//		logger.info("get Dashboard called successfully");
//		JwtResponse<?> response = new JwtResponse<>();
//		try {
//			response = dashboardService.getDashboardData(request);
//			logger.info("get Dashboard Executed Successfully");
//		return new ResponseEntity<>(response,response.getStatus());
//		}catch (Exception e) {
//			logger.error("error occured"+e);
//		}
//		response.setMessage("Something Went wrong");
//		response.setStatus("Error");
////		response.setStatusCode(HttpStatus.BAD_REQUEST);
//		return new ResponseEntity<>(response,response.getStatus());
//	}
//
//	
//}
