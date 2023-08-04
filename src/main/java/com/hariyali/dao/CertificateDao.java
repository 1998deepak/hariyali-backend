package com.hariyali.dao;

import java.util.List;
import java.util.Map;

//import com.hariyali.entity.DonationCertificate;

public interface CertificateDao {

	public Map<String,String> getCertificateByNo(int userId, String certificateNo);
	
	public List<Map<String,String>> getAllCertificatesByUserId(int userId);
	
//	public DonationCertificate saveCertificateData(DonationCertificate certificate);
}
