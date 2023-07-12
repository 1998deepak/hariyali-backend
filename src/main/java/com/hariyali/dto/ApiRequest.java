package com.hariyali.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ApiRequest {

	private JsonNode formData;

	public ApiRequest(String inputString) throws JsonProcessingException
	{
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(inputString);
		if (jsonNode.isObject())

		{
			ObjectNode objectNode = (ObjectNode) jsonNode;
			formData = objectNode.get("formData");
		}
	}
}
