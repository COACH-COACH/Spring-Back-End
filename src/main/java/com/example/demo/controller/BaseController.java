package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // RESTApi로 통신
@RequestMapping("/test")
public class BaseController {
	
	  @GetMapping("/hello")
	  public String index() {
	    return "Hello";
	  }
	
}
