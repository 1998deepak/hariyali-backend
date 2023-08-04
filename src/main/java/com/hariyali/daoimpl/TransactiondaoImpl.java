package com.hariyali.daoimpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.dao.TransactionDao;
//import com.hariyali.dto.DonorInfoRequest;
import com.hariyali.entity.Transaction;
import com.hariyali.exceptions.CustomExceptionNodataFound;
//import com.hariyali.repository.TransactionRepository;
@Component
public class TransactiondaoImpl implements TransactionDao{

//	@Autowired
//	private TransactionRepository transactionRepo;
//	

	
	@Override
	public double totalDonationAmountOfEveryDonor() {
		return 0 ;
	}

	
//	@Override
//	public List<DonorInfoRequest> getAllDonationOfGivenUser(int userId) {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		
//		List<DonorInfoRequest> donationTableOfGivenUser = objectMapper.convertValue(this.transactionRepo.donationTableOfUser(userId),new TypeReference<List<DonorInfoRequest>>() {});
//
//		
//		if(donationTableOfGivenUser!=null)
//		{
//			return donationTableOfGivenUser;
//		}
//		
//		else
//		{
//			throw new CustomExceptionNodataFound("Unable to Fetch Donation Data Of Given User");
//			
//		}
//	}
//
//	@Override
//	public DonorInfoRequest getTransactionDataByReceiptNo(String receiptNo) {
//		
//		if (receiptNo == null || receiptNo.isEmpty()) {
//	        throw new IllegalArgumentException("Receipt number cannot be null or empty");
//	    }
//		
//		 String receiptExists = transactionRepo.isReceiptNoExists(receiptNo);
//		 if(receiptExists!=null)
//		 {
//			 
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		
//		DonorInfoRequest transactionDataByReceipt = objectMapper.convertValue(this.transactionRepo.getAllTransactionDataByReciptNo(receiptNo),new TypeReference<DonorInfoRequest>() {});
//		
//		
//		if(transactionDataByReceipt!=null)
//		{
//			return transactionDataByReceipt;
//		}
//		else
//		{
//			throw new CustomExceptionNodataFound("Unable to Fetch Transaction Data by Receipt");
//		}
//		 }
//		 else
//		 {
//			 throw new CustomExceptionNodataFound("Given Receipt Number Doesn't Exists");
//				
//		 }
//	}
//
//	@Override
//	public List<DonorInfoRequest> totalDonationMadeByDonor() {
//
//		
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		List<DonorInfoRequest> totalDonation = objectMapper.convertValue(this.transactionRepo.totalDonationByDonor(),new TypeReference<List<DonorInfoRequest>>() {});
//		
//		
//		if(totalDonation!=null)
//		{
//			return totalDonation;
//		}
//		else
//		{
//			throw new CustomExceptionNodataFound("Unable to Fetch Total Donation of donor"); 
//			
//		}
//	}
//
//	@Override
//	public List<DonorInfoRequest> ListOfAllDonorWithLetestDonation() {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		
//		List<DonorInfoRequest> donorInfoList= objectMapper.convertValue(this.transactionRepo.donorList(),new TypeReference<List<DonorInfoRequest>>() {});
//	
//		
//		if(donorInfoList!=null)
//		{
//			return donorInfoList;
//		}
//		else
//		{
//			throw new CustomExceptionNodataFound("Unable to Fetch Donor List"); 
//			
//		}
//	}
//
//	@Override
//	public List<Transaction> getAllByOrderByTransactionIdDesc() {
//
//		return this.transactionRepo.findAllByOrderByMyTransactionIdDesc();
//	}
//
//	@Override
//	public List<DonorInfoRequest> getTransactionDataByUserId(int userId) {
//		
//			ObjectMapper objectMapper = new ObjectMapper();
//			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//			 List<DonorInfoRequest> allReportData= objectMapper.convertValue(this.transactionRepo.getAllTransactionDataByUserId(userId),new TypeReference<List<DonorInfoRequest>>() {});
//			 if(allReportData!=null)
//			 {
//				 return allReportData;
//			 }
//			 else
//			 {
//				 throw new CustomExceptionNodataFound("Unable to Fetch Transaction Data"); 
//			 }
//		
//		
//	}

	@Override
	public Transaction saveTransactionData(Transaction transaction) {
		
		return null;
	}

	
	
	

	
	
}
