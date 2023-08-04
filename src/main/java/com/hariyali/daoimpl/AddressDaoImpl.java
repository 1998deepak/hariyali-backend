package com.hariyali.daoimpl;

import com.hariyali.dao.AddressDao;
import com.hariyali.dto.AddressDTO;
import com.hariyali.entity.Address;
import com.hariyali.repository.AddressRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class AddressDaoImpl implements AddressDao{

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	ModelMapper mapper;
	
	@Override
	public Address saveAddress(Address addressResponse) {
//		Address savedAddress = mapper.map(addressResponse, Address.class);

		System.out.println("addressResponse"+addressResponse);
		addressRepository.save(addressResponse);


		return addressResponse;
	}
	
	
//	@Override
//	public Address saveAddress(Address addressResponse) {
//		System.out.println("Address Entity :"+addressResponse);
//		this.addressRepository.save(addressResponse);
//		return addressResponse;
//	}
	

}
