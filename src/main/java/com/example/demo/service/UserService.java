package com.example.demo.service;
//import java.util.Optional;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.model.enums.Sex;
import com.example.demo.repository.UserRepo;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Service
public class UserService {
	
	private final UserRepo urepo;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserService(UserRepo urepo, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.urepo = urepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
	

//	@Transactional
//	public void addUser(UserDto dto) {
//		User user = dto.toEntity();
//		
//		if (urepo.findById(user.getId()).isPresent()) {
//			throw new RuntimeException("이미 존재하는 사용자입니다.");
//		}
//		// 저장
//		urepo.saveAndFlush(user);
//	}
	
	public void addUser(UserDto dto) {

		String seq = null;
        String loginId = dto.getLoginId();
        String loginPw = dto.getLoginPw();
    	String fullName = dto.getFullName();
    	Sex sex = dto.getSex();
    	Date birthDate = dto.getBirthDate();
    	String region = dto.getRegion();
    	Date registDate = new Date();
    	LifeStage lifeStage = dto.getLifeStage();

        Boolean isExist = urepo.existsByLoginId(loginId);
        
        if (isExist) {
            return;
        }

        User data = new User();
        
        data.setSeq(seq);
        data.setLoginId(loginId);
        data.setLoginPw(bCryptPasswordEncoder.encode(loginPw));
        data.setFullName(fullName);
        data.setSex(sex);
        data.setBirthDate(birthDate);
        data.setRegion(region);
        data.setRegistDate(registDate);
        data.setLifeStage(lifeStage);

        urepo.save(data);
    }
	
	public UserDto getUser(Integer id) {
		User user = urepo.findById(id).orElseThrow(() -> new RuntimeException("해당 ID에 해당하는 사용자를 찾을 수 없습니다."));
		return user.toDto();
	}
	
	public void deleteUser(Integer id) {
		urepo.deleteById(id);
	}

}
