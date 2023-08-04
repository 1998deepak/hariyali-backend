package com.hariyali.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomException  extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message ;
	
	
}
