package com.hariyali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Plantation;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {

}
