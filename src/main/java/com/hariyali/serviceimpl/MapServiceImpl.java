package com.hariyali.serviceimpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.EnumConstants;
import com.hariyali.dao.MapDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.MapRequest;
import com.hariyali.entity.MapEntity;
import com.hariyali.entity.Transaction;
import com.hariyali.entity.Users;
import com.hariyali.service.MapService;
import java.util.Collections;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MapServiceImpl implements MapService {

	@Autowired
	private MapDao mapDao;

	
	
	
	@Override
	public ApiResponse<MapRequest> saveMap(MapRequest mapDetails) {
		
		ApiResponse<MapRequest> result = new ApiResponse<>();
		
		MapRequest savedMapDetails = this.mapDao.saveMapDetails(mapDetails);
		if(savedMapDetails!=null)
		{
		result.setMessage("Map Details Added Successfully");
		result.setStatus("Success");
		result.setStatusCode(HttpStatus.CREATED.value());
		result.setData(savedMapDetails);
		}
		return result;

	}

	
}
