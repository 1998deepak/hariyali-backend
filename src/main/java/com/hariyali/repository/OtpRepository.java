package com.hariyali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.OtpModel;

//@EnableJpaRepositories
@Repository
public interface OtpRepository extends JpaRepository<OtpModel, Integer>{

	@Query(value="select * from tbl_otp where donarIdOrEmail=? and otpCode=?",nativeQuery=true)
	OtpModel findBydonarIdOrEmail(String donarIdOrEmail, String otp);
	
	@Query(value="select * from tbl_otp where otpCode=?",nativeQuery=true)
	OtpModel findByOtp(String otp);

	
}
