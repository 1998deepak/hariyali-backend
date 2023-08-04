//package com.hariyali.controller;
//
//import java.util.List;
//import javax.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.dto.DonorInfoRequest;
//import com.hariyali.service.DonationCertificateInterface;
//
//@RestController
//@RequestMapping("/api/v1/")
//public class DonationCertificateController {
//
//	@Autowired
//	private DonationCertificateInterface certificateService;
//
//	// method to get all the certificates of currently logged-In user
//	@GetMapping("getAllCertificatesByUser")
//	public ResponseEntity<?> getAllUserCertificate(HttpServletRequest request) {
//
//		ApiResponse<List<DonorInfoRequest>> response = new ApiResponse<>();
//		response = this.certificateService.getAllCertificateByUserId(request);
//
//		if ("Error".equals(response.getStatus())) {
//			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//		} else {
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}
//
//	// method to get the certificate of donor by certificate number
//	@GetMapping("getCertificateByNumber/{certificateNo}")
//	public ResponseEntity<?> getCertificateByUser( HttpServletRequest request,@PathVariable String certificateNo) {
//
//		ApiResponse<DonorInfoRequest> response = new ApiResponse<>();
//
//		response = this.certificateService.getCertificateByNumber(request, certificateNo);
//
//		if ("Error".equals(response.getStatus())) {
//			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//		} else {
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}
//
//}
