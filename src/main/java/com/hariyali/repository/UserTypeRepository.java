package com.hariyali.repository;

import com.hariyali.entity.Roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeRepository extends JpaRepository<Roles,Long>{

	public Roles findByUsertypeId(int usertypId);
	
	public Roles findByUsertypeName(String usertypeName);
}
