package com.hariyali.repository;

import com.hariyali.entity.UserPackages;

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

	@Transactional
	@Modifying
	@Query(value = "update tbl_user_packages set  isPlanted=true where package_id=?1", nativeQuery = true)
	public void update(long packageId);

}
