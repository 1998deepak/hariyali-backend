package com.hariyali.dto;

import java.util.Date;


import com.hariyali.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoriesRequest {

	private int storyId;

	private String description;

	private Date createdDate;
	private String createdBy;

	private Date modifiedDate;

	private String modifiedBy;

	private Boolean isDeleted = Boolean.FALSE;

	private Users usersStories;

}
