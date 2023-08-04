package com.hariyali.service;

import java.util.List;
import java.util.Map;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.StoriesRequest;
import com.hariyali.entity.Stories;

import javax.servlet.http.HttpServletRequest;

public interface StoriesService {

	public ApiResponse<StoriesRequest> saveStory(StoriesRequest stories, HttpServletRequest request);

	public ApiResponse<StoriesRequest> deleteStory(int storyId);

	public ApiResponse<List<StoriesRequest>> getAllStoryByUserId(HttpServletRequest request);

}
