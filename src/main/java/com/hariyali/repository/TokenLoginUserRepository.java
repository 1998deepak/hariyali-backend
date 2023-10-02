package com.hariyali.repository;

import com.hariyali.entity.TokenLoginUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenLoginUserRepository extends JpaRepository<TokenLoginUser,Long> {

	@Query(value = "select * from tbl_token_login_user where username_donor_id = :donorId and flag = '0' limit 1", nativeQuery = true)
	public TokenLoginUser findByDonorId(@Param("donorId") String donorId);

	@Query(value = "select * from tbl_token_login_user where username_email_id = :emailId and flag = '1' limit 1", nativeQuery = true)
	public TokenLoginUser findByUsernameEmailId(@Param("emailId") String emailId);
}
