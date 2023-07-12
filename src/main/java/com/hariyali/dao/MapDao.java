package com.hariyali.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hariyali.dto.MapRequest;
import com.hariyali.entity.MapEntity;

public interface MapDao {

	  
	  public MapRequest saveMapDetails(MapRequest mapDetails);
	  
	  
}
