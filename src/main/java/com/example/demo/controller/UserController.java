package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserDto;
import com.example.demo.service.UserService;

//import lombok.extern.slf4j.Slf4j;

@RestController
@ResponseBody
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	// 1. 데이터 조회
	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Integer id) {
		return userService.getUser(id);
	}
	
	// 2. 데이터 삽입 // 
	@PostMapping("/join")
	public String addUser(@RequestBody UserDto userDto) {
		// @RequestBody는 json으로 들어오는 바디 데이터를 파싱하는 역할이라서
		// postman의 form-data로 보냈을 경우 에러남 - json으로 보내기
		userService.addUser(userDto);
		
		System.out.println("ok");
		return "ok";
	}
	
	// 3. 데이터 삭제
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Integer id) {
		userService.deleteUser(id);
	}
	
}
