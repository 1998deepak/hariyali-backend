package com.hariyali.repository;

import com.hariyali.entity.UserPackages;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackages, Integer> {

	public UserPackages findByCreatedBy(String createdAndUpdatedBy);

	@Query(value = "select * from tbl_user_packages where donationId=?", nativeQuery = true)
	public List<UserPackages> findPackageByDonationId(int donationId);

	@Query(value = "SELECT * FROM tbl_user_packages p INNER JOIN tbl_donation d ON p.donationId = d.donation_Id \n" +
			"WHERE p.no_of_bouquets > ifNull(plant_allocated, 0) AND d.created_date BETWEEN ?1 AND ?2 AND d.is_approved = true AND d.approval_date <= ?3", nativeQuery = true)
	public List<UserPackages> findAllPendingPackages(Date fromDate, Date toDate, Date approvalDate);

	@Transactional
	@Modifying
	@Query(value = "update tbl_user_packages set  isPlanted=true where package_id=?1", nativeQuery = true)
	public void update(long packageId);

	@Transactional
	@Modifying
	@Query(value = "update tbl_user_packages set  isPlanted=true, modified_date = now(), modified_by=?2, plant_allocated = ?3  where package_id=?1", nativeQuery = true)
	public void update(long packageId, String modifiedBy, Integer plantAllocated);

}
