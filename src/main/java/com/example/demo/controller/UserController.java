package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.FlaskConfig;
import com.example.demo.model.dto.UserDto;
import com.example.demo.service.UserService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.StatusCode;

import lombok.extern.slf4j.Slf4j;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	
	@Autowired
	private FlaskConfig flaskConfig;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	public UserController(UserService userService, RestTemplate restTemplate) {
		this.userService = userService;
		this.restTemplate = restTemplate;
	}
	
	@PostMapping("/test")
	public String testPost() {
		System.out.println("post test api 호출");
		return userService.postTest();
	}
	
	// 1. 데이터 조회
	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Integer id) {
		return userService.getUser(id);
	}
	
	// 2. 데이터 삽입 - 회원 가입
	@PostMapping("/join")
	public ResponseEntity<DefaultResponse<UserDto>> addUser(@RequestBody UserDto userDto) {
		// @RequestBody는 json으로 들어오는 바디 데이터를 파싱하는 역할이라서
		// postman의 form-data로 보냈을 경우 에러남 - json으로 보내기
		try {
			UserDto resDto = userService.addUser(userDto);
			
			log.info("SignUp API: {}, {}, {}", kv("seq", userDto.getSeq()), kv("fullname", userDto.getFullName()), kv("lifestage", userDto.getLifeStage()));
			
			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.CREATED_USER, resDto));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(DefaultResponse.res(StatusCode.BAD_REQUEST, e.getMessage()));
		}
	}
	
	// 6. 사용자 정보 수정
    @PutMapping("/modify")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
    	
    	// 현재 로그인한 유저의 토큰으로, USER_TB의 정보 알아내기
		String username = SecurityUtil.getUsername();
    	
        userService.updateUser(username, userDto);
        return ResponseEntity.ok(userDto);
    }
    
    // 7. 토큰으로 ELK 그래프 그리는데 필요한 SEQ, LFIE_STAGE 정보 받아오기
    @GetMapping("/data")
    public ResponseEntity<UserDto> getUserData() {
    	// 현재 로그인한 유저의 토큰으로, USER_TB의 ID_PK 값 알아내기
		String username = SecurityUtil.getUsername();
		int userId = userService.getUserId(username);

        // 사용자 정보를 이용하여 querySEQ와 queryLifeStage 설정
        UserDto userData = userService.getUser(userId);

        return ResponseEntity.ok(userData);
    }
	
	// 3. 사용자 비활성화
    @GetMapping("/deactivate")
	public void deleteUser() {
    	
    	// 현재 로그인한 유저의 토큰으로, USER_TB의 정보 알아내기
    	String username = SecurityUtil.getUsername();

    	userService.deactivateUser(username);
	}
    
    // 다음 분기 소비 예측
	@GetMapping("/invoke-flask")
    public String invokeFlaskServer() {
		String username = SecurityUtil.getUsername();
		try {
			String nextQuaterPayment = userService.getPredictPayment(username);
			return nextQuaterPayment;
		} catch(Exception e) {
			System.out.println(e);
			return "다음 분기 소비량 예측에 실패했습니다.";
		}
    }
}
