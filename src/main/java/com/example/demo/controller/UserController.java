package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserDto;
import com.example.demo.service.UserService;

//import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/user")
public class UserController {
	private final UserService service;
	
	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}
	
	// 1. 데이터 조회
	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Integer id) {
		return service.getUser(id);
	}
	
	// 2. 데이터 삽입
	@PostMapping("/join")
	public void addUser(@RequestBody UserDto userDto) {
		service.addUser(userDto);
	}
	
	// 3. 데이터 삭제
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Integer id) {
		service.deleteUser(id);
	}
	
}
