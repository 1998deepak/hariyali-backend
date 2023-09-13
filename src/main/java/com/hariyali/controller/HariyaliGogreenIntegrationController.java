package com.hariyali.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hariyali.dto.HariyaliGogreenIntegrationDTO;

@RestController
@RequestMapping("/api/v1")
public class HariyaliGogreenIntegrationController {

	@Value("${frontend.redirect-gogreen-url}")
	String frontendRedirectGogreenURL;

	@PostMapping("/hariyaliGogreenTrasaction")
	public void hariyaliGogreenTrasaction(@RequestBody HariyaliGogreenIntegrationDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		try {
//			ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://fullstackdeveloper.guru")).build();

			response.sendRedirect(
					frontendRedirectGogreenURL + "?meconnectId=" + dto.getMeconnectId() + "&source=" + dto.getSource());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
