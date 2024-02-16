package com.alex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.alex.dto.RealtimeDataProjection;
import com.alex.service.Mq4DataService;

@Controller
public class HomeController {
	@Autowired
	Mq4DataService mq4Service;
	
	@GetMapping("/index")
	public String index (Model model) {
		List<RealtimeDataProjection> result = mq4Service.getRealtimeData();
		model.addAttribute("result", result);
		return "index";
	}
}
