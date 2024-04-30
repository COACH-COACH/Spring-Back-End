package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.example.demo.model.dto.PaymentDto;
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
	
	// 4. 소비 예측할 사람의 분기 별 소비 내역 가져오기
	@GetMapping("/timeSeriesPrediction/{id}")
	public List<PaymentDto> getPaymentsByCustomerId(@PathVariable int id) {
        return userService.getPaymentsByCustomerId(id);
    }
	
	// 5. flask와 통신 후 예측 완료된 값 return
	@GetMapping("/invoke-flask")
    public String invokeFlaskServer() {
		String url = flaskConfig.flaskUrl + "/timeSeries"; // Flask 서버의 엔드포인트 URL
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		// 현재 로그인한 유저의 토큰으로, USER_TB의 ID_PK 값 알아내기
		String username = SecurityUtil.getUsername();
		int userId = userService.getUserId(username);

		// Spring Boot 애플리케이션에서 데이터를 가져오는 로직
        // 예를 들어, 특정 URL을 호출하여 데이터를 가져온다고 가정
		ResponseEntity<String> data = restTemplate.exchange(
				"http://localhost:8080/user/timeSeriesPrediction/" + userId, 
				HttpMethod.GET, 
				new HttpEntity<>(headers), 
				String.class);

        // Flask 서버로 데이터를 전송하여 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, data, String.class);

        return response.getBody();
    }
}
