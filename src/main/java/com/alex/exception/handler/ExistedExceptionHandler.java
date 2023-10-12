package com.alex.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alex.dto.Response;
import com.alex.exception.ExistedException;

@ControllerAdvice
public class ExistedExceptionHandler {
	@ExceptionHandler
	public ResponseEntity<Response> handlerExistedException(ExistedException existedException) {
		Response response = new Response();
		response.setMessage(existedException.getMessage());
		response.setStatus(HttpStatus.IM_USED.value());
		response.setTimeStamp(System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.IM_USED);
	}
}
