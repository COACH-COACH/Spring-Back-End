package com.example.demo.model.dto;

import java.util.Date;
//import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.entity.User;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.model.enums.Sex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserDto {
	private int id;
	private String seq;
	private String loginId;
	private String loginPw;
	private String fullName;
	private Sex sex;
	private Date birthDate;
	private String region;
	private Date registDate;
	private LifeStage lifeStage;
	private Boolean activeStatus;
	
	public User toEntity() {
		User user = new User();
		user.setId(this.getId());
		user.setSeq(this.getSeq());
		user.setLoginId(this.getLoginId());
		user.setLoginPw(this.getLoginPw());
		user.setFullName(this.getFullName());
		user.setSex(this.getSex());
		user.setBirthDate(this.getBirthDate());
		user.setRegion(this.getRegion());
		user.setRegistDate(this.getRegistDate());
		user.setLifeStage(this.getLifeStage());
		user.setActiveStatus(this.getActiveStatus());
		return user;
	}

}