package com.hariyali.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Country;
@Repository
public interface CountryRepository extends JpaRepository<Country, Long>{

	List<Country> findByIsActive(boolean b);


}
