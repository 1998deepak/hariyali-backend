package com.hariyali.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private String token;
	private String role;

	private String status;
	private int statusCode;

	private T data;
	private String message;
	private String encRequest;
	private String accessCode;
	private String gatewayURL;
	private Integer totalPages;
	private Long totalRecords;
}
