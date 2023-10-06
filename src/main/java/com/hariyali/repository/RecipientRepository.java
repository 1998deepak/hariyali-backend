package com.hariyali.repository;

import com.hariyali.entity.Recipient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipientRepository extends JpaRepository<Recipient, Integer>{

	public Recipient findByCreatedBy(String createdBy);
	
	@Query(value = "SELECT *\r\n"
			+ "FROM tbl_recipient AS recipient\r\n"
			+ "WHERE recipient.created_date = (\r\n"
			+ "  SELECT MAX(created_date)\r\n"
			+ "  FROM tbl_recipient\r\n"
			+ "  WHERE DATE(created_date) = (\r\n"
			+ "    SELECT DATE(MAX(created_date))\r\n"
			+ "    FROM tbl_recipient\r\n"
			+ "    WHERE donationId = ?1\r\n"
			+ "  )\r\n"
			+ ") AND recipient.donationId=?1\r\n"
			+ "ORDER BY TIME(created_date) DESC\r\n"
			+ "LIMIT 1;",nativeQuery = true)
	public Recipient getRecipientByDonationId(int donationId);
	
	@Query(value = "SELECT *\r\n"
			+ "FROM tbl_recipient AS recipient\r\n"
			+ "WHERE recipient.modified_date = (\r\n"
			+ "  SELECT MAX(modified_date)\r\n"
			+ "  FROM tbl_recipient\r\n"
			+ "  WHERE DATE(modified_date) = (\r\n"
			+ "    SELECT DATE(MAX(modified_date))\r\n"
			+ "    FROM tbl_recipient\r\n"
			+ "    WHERE donationId = ?1\r\n"
			+ "  )\r\n"
			+ ") AND recipient.donationId=?1\r\n"
			+ "ORDER BY TIME(modified_date) DESC\r\n"
			+ "LIMIT 1;",nativeQuery = true)
	public Recipient getLatestRecipientByDonationId(int donationId);
	
	
	@Query(value="select * from tbl_recipient where donationId=?",nativeQuery=true)
	public List<Recipient> getRecipientDataByDonationId(int donationId);

	public Recipient findByEmailId(String emailId);
}
