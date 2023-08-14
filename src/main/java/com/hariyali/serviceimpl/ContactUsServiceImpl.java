/**
 * 
 */
package com.hariyali.serviceimpl;



import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import com.hariyali.entity.ContactUs;
import com.hariyali.repository.ContactUsRepository;

/**
 * @author Dell
 *
 */
@Service
public class ContactUsServiceImpl{
	
	@Autowired
	ContactUsRepository  contactUsRepository;

	public ContactUs createContact(ContactUs contactUs) {
		return contactUsRepository.save(contactUs);
	}
	
}
