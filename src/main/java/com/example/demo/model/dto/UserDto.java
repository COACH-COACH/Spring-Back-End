package com.example.demo.model.dto;

import java.util.Date;
//import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Sex;
import com.example.demo.model.enums.LifeStage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
	private int id;
	private String loginId;
	private String loginPw;
	private String fullName;
//	private String sex;
	private Sex sex;
	private Date birthDate;
	private String region;
	private Date registDate;
	private LifeStage lifeStage;
	
	public User toEntity() {
		User user = new User();
		user.setId(this.getId());
		user.setLoginId(this.getLoginId());
		user.setLoginPw(this.getLoginPw());
		user.setFullName(this.getFullName());
		user.setSex(this.getSex());
		user.setBirthDate(this.getBirthDate());
		user.setRegion(this.getRegion());
		user.setRegistDate(this.getRegistDate());
		user.setLifeStage(this.getLifeStage());
		return user;
	}

}