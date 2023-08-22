package com.hariyali.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.dto.ReceiptDto;
import com.hariyali.entity.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer>{
	
	
	@Query(value ="SELECT JSON_ARRAYAGG(JSON_OBJECT(\r\n"
			+ "    'receiptId', r.recieptId,\r\n"
			+ "    'receiptDate', r.reciept_date,\r\n"
			+ "    'receiptNumber', r.reciept_number,\r\n"
			+ "    'receiptPath', r.reciept_Path,\r\n"
			
			+ "    'donationInfo', JSON_OBJECT(\r\n"
			+ "        'donationId', d.donation_id,\r\n"
			+ "        'donationDate', d.donation_date,\r\n"
			+ "        'donationType', d.donation_type,\r\n"
			+ "        'donationMode', d.donation_mode,\r\n"
			+ "        'totalAmount', d.total_amount,\r\n"
			+ "        'donorType', users.donor_type,\r\n"
			+ "        'firstName', users.first_name,\r\n"
			+ "        'lastName', users.last_name,\r\n"
			+ "        'organisation', users.organisation,\r\n"
			+ "        'panCard', users.pan_card,\r\n"
			+ "        'prefix', users.prefix,\r\n"
			+ "        'emailId', users.emailId,\r\n"
			+ "        'donorId', users.donorId\r\n"
			+ "    )\r\n"
			+ ")) AS Result\r\n"
			+ "FROM tbl_donation d\r\n"
			+ "INNER JOIN tbl_user_master users ON d.userId = users.user_id\r\n"
			+ "INNER JOIN tbl_reciept r ON d.donation_id = r.donation_id;",nativeQuery = true)
	Object  getAllReciept();

	@Query(value="SELECT * FROM tbl_reciept where donation_id=?", nativeQuery = true)
	Receipt getReceiptByDonation(int receiptId);
	
	@Query(value="SELECT u.user_id,d.donation_id,r.recieptId,\r\n"
			+ "r.reciept_Path,r.reciept_number\r\n"
			+ "FROM tbl_user_master u\r\n"
			+ "JOIN tbl_donation d ON u.user_id = d.userId\r\n"
			+ "JOIN tbl_reciept r ON d.donation_id = r.donation_id\r\n"
			+ "WHERE u.user_id = ?",nativeQuery = true)
	Receipt getUserReceipt(int userID);
	
	@Query(value="SELECT u.user_id,d.donation_id,r.recieptId,\r\n"
			+ "r.reciept_Path,r.reciept_number\r\n"
			+ "FROM tbl_user_master u\r\n"
			+ "JOIN tbl_donation d ON u.user_id = d.userId\r\n"
			+ "JOIN tbl_reciept r ON d.donation_id = r.donation_id\r\n"
			+ "WHERE u.user_id = ? and d.donation_id =?",nativeQuery = true)
	Receipt getUserReceiptbyDonation(int userID,int donationId);

}
