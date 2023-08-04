package com.hariyali.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

	Address findByAddressId(int addressId);
	
	
	@Query(value="select * from tbl_address where recipientId=?",nativeQuery=true)
	Address findAddressByRecipientId(int recipientId);
	
	@Query(value="select * from tbl_address where userId=?",nativeQuery=true)
	Address findAddressByUserId(int userId);
	
}
