package com.hariyali.serviceimpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hariyali.entity.OtpModel;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.EmailNotConfiguredException;
import com.hariyali.repository.OtpRepository;
import com.hariyali.repository.UsersRepository;

@Service
public class OtpServiceImpl {

	private static final int OTP_LENGTH = 6;

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	OtpRepository otpRepository;

	@Autowired
	CCServiceEmailAPI ccServiceEmailAPI;

	public void sendOtpByEmail(String emailId) {
		Users user = userRepository.findByEmailId(emailId);
		OtpModel otpModel = new OtpModel();
		if (user == null) {
			throw new IllegalArgumentException("User not found with email: " + emailId);
		}

		String otp = generateOtp();
		otpModel.setOtpCode(otp);
		otpModel.setDonarIdOrEmail(emailId);
		otpModel.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
		otpModel.setCreatedDate(LocalDateTime.now().plusMinutes(10));
		otpModel.setUsers(user);
		otpRepository.save(otpModel);
		String body = "Dear Donor,<br><br>" + "You have are accessing the profile on Hariyali website."
				+ "<br>Please use OTP for logging in - " + otp + "<br><br>-Team Hariyali<br><br>"
				+ "PS: For any support or queries please reach out to us at <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a>";
		sendEmail(emailId,"Project Hariyali-Login OTP", body);
	}
	
	public void resendOtpByEmail(String emailId) {
		Users user = userRepository.findByEmailId(emailId);
		OtpModel otpModel = new OtpModel();
		if (user == null) {
			throw new IllegalArgumentException("User not found with email: " + emailId);
		}
		OtpModel oldOtp = otpRepository.getOldOtpByEmail(emailId);
		oldOtp.setOtpCode(null);
		oldOtp.setOtpExpiryTime(null);
		otpRepository.save(oldOtp);
		String otp = generateOtp();
		otpModel.setOtpCode(otp);
		otpModel.setDonarIdOrEmail(emailId);
		otpModel.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
		otpModel.setCreatedDate(LocalDateTime.now().plusMinutes(10));
		otpModel.setUsers(user);
		otpRepository.save(otpModel);
		String body = "Dear Donor,<br><br>" + "You have are accessing the profile on Hariyali website."
				+ "<br>Please use new OTP - " + otp + "<br><br>-Team Hariyali<br><br>"
				+ "PS: For any support or queries please reach out to us at <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a>";
		sendEmail(emailId,"Project Hariyali-New OTP", body);
	}

	private String generateOtp() {
		Random random = new Random();
		int otpValue = random.nextInt((int) Math.pow(10, OTP_LENGTH));
		return String.format("%0" + OTP_LENGTH + "d", otpValue);
	}

	private void sendEmail(String toEmail,String subject, String body) {

		try {
			ccServiceEmailAPI.sendCorrespondenceMail(toEmail, subject, body);
		} catch (EmailNotConfiguredException e) {
			throw new EmailNotConfiguredException(e.getMessage());
		}
	}

	public OtpModel findBydonarIdOrEmail(String donarIdOrEmail, String otp) {
		return otpRepository.findBydonarIdOrEmail(donarIdOrEmail, otp);
	}

	public OtpModel findByOtp(String otp) {
		return otpRepository.findByOtp(otp);
	}

	public OtpModel getOtpByEmail(String donarIdOrEmail, String otp) {
		// TODO Auto-generated method stub
		return otpRepository.findBydonarIdOrEmail(donarIdOrEmail,otp);
	}
	
	public OtpModel findByEmailOtp(String email) {
		return otpRepository.findByEmailOtp(email);
	}
}
