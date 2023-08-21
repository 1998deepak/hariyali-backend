/**
 * 
 */
package com.hariyali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.ContactUs;
import com.hariyali.entity.Users;

/**
 * @author Dell
 *
 */
@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Integer>{
	
	ContactUs findByContactEmail(String contactEmail);

}
