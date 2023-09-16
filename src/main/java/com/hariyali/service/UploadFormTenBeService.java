package com.hariyali.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


public interface UploadFormTenBeService {

	void uploadFormTenBe(MultipartFile file) throws IOException;

}
