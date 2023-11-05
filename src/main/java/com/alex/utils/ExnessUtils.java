package com.alex.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alex.dto.AuthResponse;
import com.alex.dto.DataItem;
import com.alex.dto.LoginRequest;
import com.alex.service.CommissionService;
import com.alex.service.ExnessService;
import com.alex.service.UserService;
import com.alex.user.Commission;
import com.alex.user.Exness;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
