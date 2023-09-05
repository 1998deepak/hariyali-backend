package com.hariyali.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Plantation;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {
	
	@Query(value = "select * from tbl_plantation where donationId =?",nativeQuery = true)
	 List<Plantation> getPlantationByDonationId(Long donationId);

	@Query(value = "SELECT \r\n"
			+ "    tum.user_id AS user,\r\n"
			+ "    td.donation_id AS donation,\r\n"
			+ "    tup.package_id AS packages,\r\n"
			+ "    tum.emailId AS userName,\r\n"
			+ "    tup.package_name AS packageName,\r\n"
			+ "    tup.amount AS amount,\r\n"
			+ "    td.donation_type AS donationType,\r\n"
			+ "    tum.donorId AS donarId,\r\n"
			+ "    tup.no_of_bouquets AS noOfBuckets\r\n"
			+ "FROM\r\n"
			+ "    tbl_user_master tum\r\n"
			+ "        INNER JOIN\r\n"
			+ "    tbl_donation td ON tum.user_id = td.userId\r\n"
			+ "        INNER JOIN\r\n"
			+ "    tbl_user_packages tup ON tup.donationId = td.donation_id\r\n"
			+ "WHERE\r\n"
			+ "    tup.isPlanted =false\r\n"
			+ "    and tup.package_name='Monsoon'\r\n"
			+ "    and td.created_date BETWEEN '2021-04-01' AND '2022-03-31'" ,nativeQuery = true)
	List<Map<String,Object>> getUserDonationAndPlantationData();


}
