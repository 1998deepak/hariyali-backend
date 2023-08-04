package com.hariyali.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hariyali.entity.Transaction;

public interface TransactionDao {

	public double totalDonationAmountOfEveryDonor();
	
	
//	public List<DonorInfoRequest> getAllDonationOfGivenUser(int userId);
//	
//	public DonorInfoRequest getTransactionDataByReceiptNo(String receiptNo);
//	
//	public List<DonorInfoRequest> totalDonationMadeByDonor();
//	
//	public List<DonorInfoRequest> ListOfAllDonorWithLetestDonation();
//	
//	public List<Transaction> getAllByOrderByTransactionIdDesc();
//	
//	public List<DonorInfoRequest> getTransactionDataByUserId(int userId);
	
	public Transaction saveTransactionData(Transaction transaction);
}
