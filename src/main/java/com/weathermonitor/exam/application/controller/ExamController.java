package com.weathermonitor.exam.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weathermonitor.exam.application.service.ExamService;


@RestController
public class ExamController {
	
	@Autowired
	ExamService examService; 
	
	@GetMapping("/exam")
	public void startApp() {
		examService.scheduleRules();
	}
}
