package com.hariyali.service;

import java.util.List;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.MapRequest;

public interface MapService {

	public ApiResponse<MapRequest> saveMap(MapRequest mapDetails);

}
