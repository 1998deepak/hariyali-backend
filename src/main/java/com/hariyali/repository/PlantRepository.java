package com.hariyali.repository;

import com.hariyali.entity.Plants;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlantRepository extends JpaRepository<Plants, Integer> {

	Plants findByPlantId(Integer plantId);
	
//	Plants findByTitle(String plantsTtitle);
	
	public Page<Plants> findAllByOrderByPlantIdDesc(Pageable paging);
	
//	Boolean existsByTitle(String plantsTitle);
}
