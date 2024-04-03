package com.example.demo.model.entity;
import java.util.Date;
import java.util.List;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.enums.Sex;
import com.example.demo.model.enums.LifeStage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data   // RequiredArgsConstructor만 가져오기 때문 
@NoArgsConstructor
@AllArgsConstructor
@Entity // h2 database에 user 라는 테이블을 만들었더니 오류 - mysql은 상관없음
@Table(name="USER_TB")
public class User {
	@Id
	@Column(name="ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="LOGIN_ID", unique=true)
	private String loginId;
	
	@Column(name="LOGIN_PW")
	private String loginPw;
	
//	@Column(name="FULL_NAME")
//	private String fullName;
//	
//	@Column(name="SEX")
//	@Enumerated(EnumType.STRING)
//	private Sex sex;
//	
//	@Column(name="BIRTH_DATE")
//	private Date birthDate;
//	
//	@Column(name="REGION")
//	private String region;
//	
//	@Column(name="REGIST_DATE")
//	private Date registDate;
//	
//	@Column(name="LIFE_STAGE")
//	private LifeStage lifeStage;

	// 관계설정
	@OneToMany(mappedBy="user", cascade = CascadeType.ALL)
	private List<Payment> payments;
	@OneToMany(mappedBy="user", cascade = CascadeType.ALL)
	private List<Goal> goals;
	@OneToMany(mappedBy="user", cascade = CascadeType.ALL)
	private List<Enroll> enrolls;
	
	// toDto() : entity -> dto
	public UserDto toDto() {
		UserDto dto = new UserDto();
		dto.setId(this.getId());
		dto.setLoginId(this.getLoginId());
		dto.setLoginPw(this.getLoginPw());
//		dto.setFullName(this.getFullName());
//		dto.setSex(this.getSex());
//		dto.setBirthDate(this.getBirthDate());
//		dto.setRegion(this.getRegion());
//		dto.setLifeStage(this.getLifeStage());
		return dto;
	}
}
