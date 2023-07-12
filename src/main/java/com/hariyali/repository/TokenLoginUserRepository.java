package com.hariyali.repository;

import com.hariyali.entity.TokenLoginUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenLoginUserRepository extends JpaRepository<TokenLoginUser,Long> {

	public TokenLoginUser findByDonorId(String donorId);
	
	public TokenLoginUser findByEmailId(String emailId);
}
