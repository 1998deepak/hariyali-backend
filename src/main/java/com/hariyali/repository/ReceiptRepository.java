package com.hariyali.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
	
	@Query(value="WITH MaxReceipt AS (\r\n"
			+ "    SELECT d.userId, MAX(r.recieptId) AS max_receiptId\r\n"
			+ "    FROM tbl_donation d\r\n"
			+ "    JOIN tbl_reciept r ON d.donation_id = r.donation_id\r\n"
			+ "    WHERE d.userId = ?\r\n"
			+ "    GROUP BY d.userId\r\n"
			+ ")\r\n"
			+ "SELECT u.user_id,\r\n"
			+ "       d.donation_id,\r\n"
			+ "       r.recieptId,\r\n"
			+ "       r.reciept_Path,\r\n"
			+ "       r.reciept_number\r\n"
			+ "FROM MaxReceipt m\r\n"
			+ "JOIN tbl_donation d ON m.userId = d.userId\r\n"
			+ "JOIN tbl_reciept r ON d.donation_id = r.donation_id AND r.recieptId = m.max_receiptId\r\n"
			+ "JOIN tbl_user_master u ON d.userId = u.user_id;",nativeQuery = true)
	Receipt getUserReceipt(int userID);
	
	@Query(value="SELECT u.user_id,d.donation_id,r.recieptId,\r\n"
			+ "r.reciept_Path,r.reciept_number\r\n"
			+ "FROM tbl_user_master u\r\n"
			+ "JOIN tbl_donation d ON u.user_id = d.userId\r\n"
			+ "JOIN tbl_reciept r ON d.donation_id = r.donation_id\r\n"
			+ "WHERE u.user_id = ? and d.donation_id =?",nativeQuery = true)
	Receipt getUserReceiptbyDonation(int userID,int donationId);

	Receipt getByRecieptNumber(String recieptNumber);
	
	@Query(value ="select recp.recieptId,recp.reciept_date,recp.reciept_number,recp.reciept_Path,recp.donation_id From tbl_reciept as recp,tbl_donation as donation,tbl_user_master as u\r\n"
			+ "			where recp.donation_id = donation.donation_id AND donation.userId = u.user_id AND u.emailId = ?",nativeQuery = true)
	List<Receipt>  getAllReciept(String emailId);
}
