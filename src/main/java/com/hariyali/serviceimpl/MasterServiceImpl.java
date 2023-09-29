package com.hariyali.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

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

		return countryRepository.findByIsActive(true).stream()
				.map(c -> new Country(c.getId(), c.getCreatedBy(), c.getCreatedDate(), c.getIsActive(),
						c.getUpdatedBy(), c.getUpdatedDate(), c.getCountryCode(), c.getCountryName().toUpperCase()))
				.collect(Collectors.toList());
	}

	@Override
	public List<State> getAllStateByCountryId(String countryCode) {

		return stateRepository.findByCountryCode(countryCode).stream()
				.map(s -> new State(s.getId(), s.getCreatedBy(), s.getCreatedDate(), s.getIsActive(), s.getUpdatedBy(),
						s.getUpdatedDate(), s.getCountryCode(), s.getCountryName(), s.getCountryId(), s.getStateCode(),
						s.getStateName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Citzenship> getAllCitizensip() {

		return citizenshipRepository.findByIsActive(true).stream().map(c -> new Citzenship(c.getId(),
				c.getCitizenshipName().toUpperCase(), null, c.getIsActive(), null, null, null))
				.collect(Collectors.toList());

	}

}
