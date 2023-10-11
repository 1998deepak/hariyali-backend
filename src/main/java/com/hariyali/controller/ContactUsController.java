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
	ContactUsServiceImpl contactUsServiceImpl;

	@Autowired
	private EmailService emailService;

	@PostMapping("/saveContact")
	public ContactUs saveContact(@RequestBody ContactUs contactUs) {
		ContactUs contact = contactUsServiceImpl.createContact(contactUs);
		emailService.sendSimpleEmailToHariyaliTeam(contactUs);
		return contact;
	}

}
