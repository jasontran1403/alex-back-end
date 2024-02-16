package com.alex.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.alex.service.CommissionService;
import com.alex.service.ExnessService;
import com.alex.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class ExnessUtils {
	@Autowired
	ExnessService exService;
	@Autowired
	UserService userSerivce;
	@Autowired
	CommissionService commissService;

	public void getIB() throws JsonMappingException, JsonProcessingException {
		
	}
}
