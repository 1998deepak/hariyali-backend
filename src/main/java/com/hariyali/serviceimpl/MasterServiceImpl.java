package com.hariyali.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hariyali.entity.Citzenship;
import com.hariyali.entity.Country;
import com.hariyali.entity.State;
import com.hariyali.repository.CitizenshipRepository;
import com.hariyali.repository.CountryRepository;
import com.hariyali.repository.StateRepository;
import com.hariyali.service.MasterService;

@Service
public class MasterServiceImpl implements MasterService {

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private CitizenshipRepository citizenshipRepository;

	@Override
	public List<Country> getAllCountry() {

		return countryRepository.findByIsActive(true);
	}

	@Override
	public List<State> getAllStateByCountryId(String countryCode) {

		return stateRepository.findByCountryCode(countryCode);
	}

	@Override
	public List<Citzenship> getAllCitizensip() {
		return citizenshipRepository.findAll();
	}

}
