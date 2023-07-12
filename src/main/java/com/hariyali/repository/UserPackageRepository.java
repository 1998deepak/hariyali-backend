package com.hariyali.repository;

import com.hariyali.entity.UserPackages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackages, Integer>{

	public UserPackages findByCreatedBy(String createdAndUpdatedBy);

	
	@Query(value="select * from tbl_user_packages where donationId=?",nativeQuery=true)
	public List<UserPackages> findPackageByDonationId(int donationId);
	
}
