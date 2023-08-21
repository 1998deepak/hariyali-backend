/**
 * 
 */
package com.hariyali.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hariyali.entity.ContactUs;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.ContactUsRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.serviceimpl.ContactUsServiceImpl;
import com.hariyali.utils.EmailService;

/**
 * @author Dell
 *
 */
@RestController
@RequestMapping("/api/v1")
public class ContactUsController {

	
	@Autowired
	ContactUsServiceImpl  contactUsServiceImpl;
	
	@Autowired
	private ContactUsRepository contactUsRepository;
	
	@Autowired
	private EmailService emailService;
	
	@PostMapping("/saveContact")
	public ContactUs saveContact(@RequestBody ContactUs contactUs) {
		ContactUs resulEntity = contactUsRepository.findByContactEmail(contactUs.getContactEmail());
		if (resulEntity == null) {
			throw new CustomExceptionNodataFound(
					"User with " + contactUs.getContactEmail() + " is not Registered");
		}
			ContactUs contact = contactUsServiceImpl.createContact(contactUs);
			emailService.sendSimpleEmailToHariyaliTeam(contact.getContactEmail(), contact.getContactSubject(), contact.getMassage());
			return contact;
	}


}
