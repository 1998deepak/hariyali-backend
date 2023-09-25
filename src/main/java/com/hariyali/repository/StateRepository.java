package com.hariyali.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

	List<State> findByCountryId(long countryId);

}
