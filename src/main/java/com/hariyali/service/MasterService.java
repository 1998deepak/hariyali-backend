package com.hariyali.service;

import java.util.List;

import com.hariyali.entity.Citzenship;
import com.hariyali.entity.Country;
import com.hariyali.entity.State;

public interface MasterService {

	public List<Country> getAllCountry();

	public List<State> getAllStateByCountryId(String countryCode);

	public List<Citzenship> getAllCitizensip();

}
