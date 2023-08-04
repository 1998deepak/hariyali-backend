//package com.hariyali.serviceimpl;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hariyali.EnumConstants;
//import com.hariyali.config.JwtHelper;
//import com.hariyali.dao.CertificateDao;
//import com.hariyali.dao.UserDao;
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.dto.DonorInfoRequest;
//import com.hariyali.dto.UsersRequest;
//import com.hariyali.service.DonationCertificateInterface;
//
//@Service
//public class DonationCertificateServiceImpl implements DonationCertificateInterface {
//
//	@Autowired
//	private CertificateDao certificateDao;
//
//	@Autowired
//	private JwtHelper jwtHelper;
//	
//	@Autowired
//	private UserDao userDao;
//	
//	@Override
//	public ApiResponse<List<DonorInfoRequest>> getAllCertificateByUserId(HttpServletRequest request) {
//		
//		ApiResponse<List<DonorInfoRequest>> result = new ApiResponse<>();
//		
//		String token = request.getHeader("Authorization");
//		String donorId = jwtHelper.getUsernameFromToken(token.substring(7));
//		UsersRequest user = this.userDao.getByDonorId(donorId);
//		
//		
//	
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//		List<DonorInfoRequest> donorInfoRequestObj= objectMapper.convertValue(
//				this.certificateDao.getAllCertificatesByUserId(user.getUserId()), new TypeReference<List<DonorInfoRequest>>() {
//				});
//		
//		if(donorInfoRequestObj!=null)
//		{
//			result.setData(donorInfoRequestObj);
//			result.setStatus(EnumConstants.SUCCESS);
//			result.setStatusCode(HttpStatus.OK.value());
//			result.setMessage("Data Fetched Successfully..");
//			return result;
//		}
//		else 
//		{
//			result.setData(null);
//			result.setStatus(EnumConstants.ERROR);
//			result.setStatusCode(HttpStatus.NOT_FOUND.value());
//			result.setMessage("No Data Found");
//			return result;
//		}
//	}
//
//	@Override
//	public ApiResponse<DonorInfoRequest> getCertificateByNumber(HttpServletRequest request, String certificateNo) {
//		
//		ApiResponse<DonorInfoRequest> result = new ApiResponse<>();
//		
//		String token = request.getHeader("Authorization");
//		String donorId = jwtHelper.getUsernameFromToken(token.substring(7));
//		UsersRequest user = this.userDao.getByDonorId(donorId);
//		
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//		DonorInfoRequest donorInfoRequestObj = objectMapper.convertValue(
//			this.certificateDao.getCertificateByNo(user.getUserId(), certificateNo),
//				new TypeReference<DonorInfoRequest>() {
//				});
//		
//		if(donorInfoRequestObj!=null)
//		{
//			result.setData(donorInfoRequestObj);
//			result.setStatus(EnumConstants.SUCCESS);
//			result.setStatusCode(HttpStatus.OK.value());
//			result.setMessage("Data Fetched Successfully..");
//			return result;
//		}
//		else 
//		{
//			result.setData(null);
//			result.setStatus(EnumConstants.ERROR);
//			result.setStatusCode(HttpStatus.NOT_FOUND.value());
//			result.setMessage("No Data Found");
//			return result;
//		}
//	}
//
//}
