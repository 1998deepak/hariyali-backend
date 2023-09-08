package com.hariyali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.PlantationMaster;

@Repository
public interface PlantationMasterRepository extends JpaRepository<PlantationMaster, Long> {

}
