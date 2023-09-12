package com.hariyali.controller;

import com.hariyali.dto.ApiResponse;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionDataAlreadyExists;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.exceptions.TooManyRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<ApiResponse> exception(TooManyRequestException exception) {
        log.error("Exception = " + exception.toString());
        return processException(exception.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(SQLException exception) {
        log.error("Exception = " + exception.toString());
        exception.printStackTrace();
        return processException("Internal server error, please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomExceptionDataAlreadyExists.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(CustomExceptionDataAlreadyExists exception) {
        log.error("Exception = " + exception.toString());
        return processException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomExceptionNodataFound.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(CustomExceptionNodataFound exception) {
        log.error("Exception = " + exception.toString());
        return processException(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(CustomException exception) {
        log.error("Exception = " + exception.toString());
        return processException(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(RuntimeException exception) {
        exception.printStackTrace();
        log.error("Exception = " + exception.toString());
        return processException("Internal server error, please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handleException(Exception exception) {
        exception.printStackTrace();
        log.error("Exception = " + exception.toString());
        return processException("Internal server error, please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse> processException(String message, HttpStatus status){
        ApiResponse response = new ApiResponse();
        response.setStatus(status.name());
        response.setMessage(message);
        return new ResponseEntity<>(response, status);
    }
}
