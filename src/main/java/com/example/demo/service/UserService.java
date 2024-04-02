package com.example.demo.service;
//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepo;

import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Service
public class UserService {
	private final UserRepo urepo;
	
	@Autowired
	public UserService(UserRepo urepo) {
		this.urepo = urepo;
	}
	
	@Transactional
	public void addUser(UserDto dto) {
		User user = dto.toEntity();
		
		if (urepo.findById(user.getId()).isPresent()) {
			throw new RuntimeException("이미 존재하는 사용자입니다.");
		}
		// 저장
		urepo.saveAndFlush(user);
	}
	public UserDto getUser(Integer id) {
		User user = urepo.findById(id).orElseThrow(() -> new RuntimeException("해당 ID에 해당하는 사용자를 찾을 수 없습니다."));
		return user.toDto();
	}
	
	public void deleteUser(Integer id) {
		urepo.deleteById(id);
	}

}
