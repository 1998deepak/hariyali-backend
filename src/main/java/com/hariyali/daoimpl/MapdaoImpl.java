package com.hariyali.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.dao.MapDao;
import com.hariyali.dto.MapRequest;
import com.hariyali.dto.PackagesRequest;
import com.hariyali.entity.MapEntity;
import com.hariyali.entity.Packages;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.MapRepository;

@Component
public class MapdaoImpl implements MapDao {

	@Autowired
	private MapRepository mapRepo;

	@Override
	public MapRequest saveMapDetails(MapRequest mapDetails) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		MapEntity map = mapper.convertValue(mapDetails, MapEntity.class);
//		map.setTimestamp(new Date());

		this.mapRepo.save(map);

		MapRequest mapRequestObj = mapper.convertValue(map, MapRequest.class);

		if (mapRequestObj != null) {
			return mapRequestObj;
		}

		else {
			throw new CustomExceptionNodataFound("Unable to save Map");
		}

	}

}
