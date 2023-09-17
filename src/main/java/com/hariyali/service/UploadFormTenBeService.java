package com.hariyali.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;


public interface UploadFormTenBeService {

	void uploadFormTenBe(MultipartFile file, HttpServletRequest request) throws IOException;

}
