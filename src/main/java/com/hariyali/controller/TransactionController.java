//package com.hariyali.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.dto.DonorInfoRequest;
//import com.hariyali.dto.PaymentRequest;
//import com.hariyali.entity.ApiRequest;
//import com.hariyali.service.TransactionService;
//import com.razorpay.RazorpayException;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/")
//public class TransactionController {
//
//	@Autowired
//	private TransactionService transactionservice;
//
//	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
//
//	// method to donate plant
//	@PostMapping("donate")
//	public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody PaymentRequest data, HttpServletRequest request)
//			throws RazorpayException {
//
//		ApiResponse<String> response = new ApiResponse<>();
//
//		response = this.transactionservice.createTransaction(data, request);
//
////		if ("Error".equals(response.getStatus())) {
////			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
////		} else {
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		//}
//
//	}
//
//	// @PostMapping("update_order")
////	public ResponseEntity<?> updateOrder(@RequestBody Map<String,Object> data,Principal principle)
////	{
////		Transaction transaction = new Transaction();
////		transaction.setUser(this.userRepo.findByDonorId(principle.getName()));
////		
////		 transaction = this.transactionRepo.findByOrderId(data.get("order_id").toString());
////
////
////		transaction.setPaymentId(data.get("payment_id").toString());
////		transaction.setTransactionStatus(data.get("status").toString());
////		this.transactionRepo.save(transaction);
////		System.out.println(data);
////		return ResponseEntity.ok(Map.of("msg","updated"));
////	}
////	
//
//	// method to get all transactions of currently logged-In user sorted by donation
//	// date in descending order
//	@GetMapping("getTransactionReceiptByUser")
//	public ResponseEntity<ApiResponse<List<DonorInfoRequest>>> getTransactionByUserId(HttpServletRequest request) {
//
//		ApiResponse<List<DonorInfoRequest>> response = new ApiResponse<>();
//
//		response = this.transactionservice.getAllTransactionDataByUserId(request);
//
//		
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		
//	}
//
//	// get perticular transaction data by receipt no
//
//	@GetMapping("getTransactionReceiptNo")
//	public ResponseEntity<ApiResponse<DonorInfoRequest>> getTransactionByReceiptNo(@RequestParam String receiptNo) {
//
//		ApiResponse<DonorInfoRequest> response = new ApiResponse<>();
//
//		response = this.transactionservice.getAllReportDataByReciptNo(receiptNo);
//
//		
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		
//
//	}
//
//	// object mapper-donor list
//	@GetMapping("donorList")
//	public ResponseEntity<ApiResponse<List<DonorInfoRequest>>> getTransactionByPaymentId() {
//
//		ApiResponse<List<DonorInfoRequest>> response = new ApiResponse<>();
//
//		response = this.transactionservice.getDonorListWithLetestDonation();
//
//			
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		
//	}
//
//	// leader board
//	@GetMapping("leaderBoard")
//	public ResponseEntity<ApiResponse<List<DonorInfoRequest>>> getLeaderBoardbyTotalDonation() {
//
//		ApiResponse<List<DonorInfoRequest>> response = new ApiResponse<>();
//
//		response = this.transactionservice.getAllDonations();
//
//		
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		
//	}
//
//	@GetMapping("donationTableUser")
//	public ResponseEntity<ApiResponse<List<DonorInfoRequest>>> getDonationTableByUserId(HttpServletRequest request) {
//
//		ApiResponse<List<DonorInfoRequest>> response = new ApiResponse<>();
//
//		response = this.transactionservice.getUserDonationTable(request);
//
//		
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		
//	}
//
//	@PostMapping("formData")
//	public ResponseEntity<ApiResponse<String>> formDataMethod(@RequestBody String inputString) throws JsonMappingException, JsonProcessingException
//	{
//			ApiRequest apiRequest = new ApiRequest(inputString);		
//			return new ResponseEntity<>(transactionservice.demo(apiRequest.getFormData()), HttpStatus.OK);
//	} 
//	
//}
