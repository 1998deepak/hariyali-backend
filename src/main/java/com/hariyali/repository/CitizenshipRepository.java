package com.hariyali.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Citzenship;

@Repository
public interface CitizenshipRepository extends JpaRepository<Citzenship, Long> {

	List<Citzenship> findByIsActive(boolean b);

}
