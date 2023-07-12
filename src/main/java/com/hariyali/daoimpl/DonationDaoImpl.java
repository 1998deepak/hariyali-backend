package com.hariyali.daoimpl;

import com.hariyali.dao.DonationDao;
import com.hariyali.entity.Donation;
import com.hariyali.repository.DonationRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DonationDaoImpl implements DonationDao {

	@Autowired
	DonationRepository donationRepository;

	@Autowired
	ModelMapper mapper;
	
	@Override
	public Donation saveDonation(Donation donationResponse) {
		Donation savedDonation = mapper.map(donationResponse, Donation.class);

		System.out.println("donationResponse"+donationResponse);
		donationRepository.save(savedDonation);

//		AddressDTO addtessRequestObj = mapper.map(savedAddress, AddressDTO.class);

		return donationResponse;
	}



}
