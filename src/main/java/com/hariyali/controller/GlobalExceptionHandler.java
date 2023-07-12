package com.hariyali.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiResponse;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionDataAlreadyExists;
import com.hariyali.exceptions.CustomExceptionNodataFound;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
		ApiResponse<Object> errorResponse = new ApiResponse<>();
		errorResponse.setStatus(EnumConstants.ERROR);
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
		errorResponse.setData(null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST) ;
    }
	
	@ExceptionHandler(CustomExceptionNodataFound.class)
	public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(CustomExceptionNodataFound ex) {
		ApiResponse<Object> errorResponse = new ApiResponse<>();
		errorResponse.setStatus(EnumConstants.ERROR);
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		errorResponse.setData(null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND) ;
    }
	
	@ExceptionHandler(CustomExceptionDataAlreadyExists.class)
	public ResponseEntity<ApiResponse<Object>> handleDataAlreadyExistsException(CustomExceptionDataAlreadyExists ex) {
		ApiResponse<Object> errorResponse = new ApiResponse<>();
		errorResponse.setStatus(EnumConstants.ERROR);
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatusCode(HttpStatus.CONFLICT.value());
		errorResponse.setData(null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT) ;
    }
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Object>> handleDataAlreadyExistsException(CustomException ex) {
		ApiResponse<Object> errorResponse = new ApiResponse<>();
		errorResponse.setStatus(EnumConstants.ERROR);
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		errorResponse.setData(null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT) ;
    }
	
	
//	@ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
//        String errorMessage = "Data integrity violation: " + ex.getMessage();
//        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
//    }
}
