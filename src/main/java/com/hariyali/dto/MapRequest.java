package com.hariyali.dto;

import java.util.Date;

import com.hariyali.entity.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapRequest {

	private Integer mapId;

	private String activity;

	private Long lat;

	private Long lng;

	private Long distance;

	private Date timestamp;

	private String sessionId;

	private Transaction transactions;

}
