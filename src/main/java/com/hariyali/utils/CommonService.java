package com.hariyali.utils;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.DonationRepository;
import com.hariyali.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired 
	private DonationRepository donationRepository;
	
	
	//method to generate new donor or donation id
	public String createDonarIDORDonationID(String idForEntity) {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        String formattedResult=null;
        int lastFiveDigits ;
        String lastFiveDigitsStr = null;
        
        if(idForEntity.equalsIgnoreCase("user")) {
        	 String lastDonarID = usersRepository.getLastDonorID();
        	 if(lastDonarID == null)
        		 return "DID20230900001";
             lastFiveDigitsStr = lastDonarID.substring(lastDonarID.length() - 5);
             lastFiveDigits = Integer.parseInt(lastFiveDigitsStr) + 1;
             formattedResult = String.format("%05d", lastFiveDigits);
             return "DN" + year + String.format("%02d", month) + formattedResult;
        }else if(idForEntity.equalsIgnoreCase("donation")){
        	  String lastDonationID = donationRepository.getLastDonationID();
        	  if(lastDonationID == null)
        		  return "DN20230900001";
              lastFiveDigitsStr = lastDonationID.substring(lastDonationID.length() - 5);
              lastFiveDigits = Integer.parseInt(lastFiveDigitsStr) + 1;
              formattedResult = String.format("%05d", lastFiveDigits);
              return "DN" + year + String.format("%02d", month) + formattedResult;
        }else {
        	log.error("send appropriate idForEntity value..!!");
        	throw new CustomException("send appropriate idForEntity value..!!");
        }
        
	}
}
