//package com.hariyali.serviceimpl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hariyali.config.JwtHelper;
//import com.hariyali.dao.TransactionDao;
//import com.hariyali.dao.UserDao;
//import com.hariyali.dto.DashboardDto;
//import com.hariyali.dto.ApiResponse;
//import com.hariyali.entity.Users;
//import com.hariyali.repository.StoriesRepository;
//import com.hariyali.service.DashboardService;
//import com.hariyali.EnumConstants;
//import javax.servlet.http.HttpServletRequest;
//@Service
//public class DashboardServiceImpl implements DashboardService{
//	
//	@Autowired
//	private TransactionDao transactionDao;
//	
//	@Autowired
//	private UserDao userDao;
//	
//	@Autowired
//	private JwtHelper jwtHelper;
//	
//
//	private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
//
//	@Override
//	public ApiResponse<DashboardDto> getDashboardData(HttpServletRequest request) {
//		logger.info("get Dashboarddata method called successfully...");
//		ApiResponse<DashboardDto> response = new ApiResponse<>();
//		String token = request.getHeader("Authorization");
//		String donorId = jwtHelper.getUsernameFromToken(token.substring(7));
//		Users user = this.userDao.getByDonorId(donorId);
//		System.out.println("dash User :"+user.getUserId());
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		try
//		
//		{
//			DashboardDto res; 
//			String role =user.getUserRole().getUsertypeName();
//			System.out.println(role);
//			
//			if(role.equals("Admin")) {
//				res = mapper.convertValue(this.transactionDao.totalDonationAmountOfEveryDonor(),DashboardDto.class);
//				System.err.println(res);
//			}
//			else {
//				res = mapper.convertValue(this.transactionDao.totalDonationMadeBySpecificUser(user.getUserId()),DashboardDto.class);
//				System.err.println(res);
//			}
//			
//			res.setDonorCount(userDao.getdonorCount());
//			response.setMessage("Data fetch Successfully");
//			response.setData(res);
//			response.setStatus(EnumConstants.SUCCESS);
//			response.setStatusCode(HttpStatus.OK.value());
//			logger.info("get Dashboard data method executed successfully...");
//			return response;
//			
//		}catch(Exception e)
//		{
//			logger.error("error occured"+e);	
//		}
//		response.setMessage("Something went wrong");
//		response.setStatus(EnumConstants.ERROR);
//		response.setStatusCode(HttpStatus.NOT_FOUND.value());
//		return response;
//	}
//
//}
