package com.hariyali.dao;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.hariyali.dto.StoriesRequest;
import com.hariyali.entity.Stories;
import com.hariyali.entity.Users;

public interface StoriesDao {

	
	public List<StoriesRequest> getStoryDataByUserId(int userId);
	
	public StoriesRequest saveStories(StoriesRequest story,Users user);
	
	public StoriesRequest deleteStory(int storyId);

	
}
