package com.hariyali.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Plantation;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {
	
	@Query(value = "select * from tbl_plantation where donationId =?",nativeQuery = true)
	 List<Plantation> getPlantationByDonationId(Long donationId);


}
