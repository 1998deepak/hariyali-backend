package com.hariyali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Commitment;


@Repository
public interface CommitmentRepository extends JpaRepository<Commitment, Long> {
	
	

}
