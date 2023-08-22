package com.hariyali.serviceimpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hariyali.entity.OtpModel;
import com.hariyali.entity.Users;
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
	private JavaMailSender javaMailSender;

	public void sendOtpByEmail(String emailId) {
		Users user = userRepository.findByEmailId(emailId);
		OtpModel otpModel = new OtpModel();
		if (user == null) {
			throw new IllegalArgumentException("User not found with email: " + emailId);
		}

		String otp = generateOtp();
		otpModel.setOtpCode(otp);
		otpModel.setDonarIdOrEmail(emailId);;
		otpModel.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
		otpModel.setUsers(user);
		otpRepository.save(otpModel);
		sendEmail(emailId, "Your OTP for login: " + otp);
	}

	private String generateOtp() {
		Random random = new Random();
		int otpValue = random.nextInt((int) Math.pow(10, OTP_LENGTH));
		return String.format("%0" + OTP_LENGTH + "d", otpValue);
	}

	private void sendEmail(String toEmail, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Login OTP");
		message.setText(body);
		javaMailSender.send(message);
	}

	public OtpModel findBydonarIdOrEmail(String donarIdOrEmail, String otp) {
		return otpRepository.findBydonarIdOrEmail(donarIdOrEmail,otp);
	}
	
	public OtpModel findByOtp(String otp) {
		return otpRepository.findByOtp(otp);
	}

	public OtpModel getOtpByEmail(String donarIdOrEmail) {
		// TODO Auto-generated method stub
		return otpRepository.getOtpByEmail(donarIdOrEmail);
	}
}
