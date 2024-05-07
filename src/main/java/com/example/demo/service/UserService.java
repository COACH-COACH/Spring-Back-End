package com.example.demo.service;
//import java.util.Optional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.FlaskConfig;
import com.example.demo.model.dto.PaymentDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Payment;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.model.enums.Sex;
import com.example.demo.repository.PaymentRepo;
import com.example.demo.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
//import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	private final UserRepo urepo;
	private final PaymentRepo prepo;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final RestTemplate restTemplate;
	
	@Autowired
    private FlaskConfig flaskConfig;
	
	@Autowired
	public UserService(UserRepo urepo, PaymentRepo prepo, BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate) {
        this.urepo = urepo;
        this.prepo = prepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.restTemplate = restTemplate;
    }
	
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 20;
    private Random random = new Random();
    
	private String createSeq() {
		StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
	}
	
	public UserDto addUser(UserDto dto) throws Exception {

		String seq = null;
        String loginId = dto.getLoginId();
        String loginPw = dto.getLoginPw();
    	String fullName = dto.getFullName();
    	Sex sex = dto.getSex();
    	Date birthDate = dto.getBirthDate();
    	String region = dto.getRegion();
    	LifeStage lifeStage = dto.getLifeStage();
    	Boolean activeStatus = true;

        Boolean isExist = urepo.existsByLoginId(loginId);
        
        if (isExist) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }

        User data = new User();
        
        data.setSeq(seq);
        data.setLoginId(loginId);
        data.setLoginPw(bCryptPasswordEncoder.encode(loginPw));
        data.setFullName(fullName);
        data.setSex(sex);
        data.setBirthDate(birthDate);
        data.setRegion(region);
        data.setRegistDate(new Date());
        data.setLifeStage(lifeStage);
        data.setActiveStatus(activeStatus);
        data.setSeq(createSeq());

        return urepo.save(data).toDto();
    }
	
	public void updateUser(String username, UserDto userDto) {
        Optional<User> optionalUser = Optional.ofNullable(urepo.findByLoginId(username));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // DTO에서 변경된 필드만 업데이트
            if (userDto.getLoginPw() != null) {
                user.setLoginPw(bCryptPasswordEncoder.encode(userDto.getLoginPw()));
            }

            if (userDto.getLifeStage() != null) {
                user.setLifeStage(userDto.getLifeStage());
            }

            // 변경된 필드를 저장
            urepo.save(user);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + username);
        }
    }
	
	public UserDto getUser(Integer id) {
		User user = urepo.findById(id).orElseThrow(() -> new RuntimeException("해당 ID에 해당하는 사용자를 찾을 수 없습니다."));
		return user.toDto();
	}
	
	// 다음 분기 소비 예측
	@Transactional
	public String getPredictPayment1(String username) {
		User user = urepo.findByLoginId(username);
		List<PaymentDto> payments = prepo.findByUser_Id(user.getId()).stream()
				.map(Payment::toDto)
				.collect(Collectors.toList());
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<List<PaymentDto>> entity = new HttpEntity<>(payments, headers);
	    log.debug("getPaymentList", entity);
		
		String url = flaskConfig.flaskUrl + "/timeSeries";
//		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                entity, String.class);
        
		return response.getBody();
	}
	
	// 수정
	@Transactional
	public String getPredictPayment(String username) {
		User user = urepo.findByLoginId(username);
		List<PaymentDto> payments = prepo.findByUser_Id(user.getId()).stream()
				.map(Payment::toDto)
				.collect(Collectors.toList());
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<List<PaymentDto>> entity = new HttpEntity<>(payments, headers);
		
		String url = flaskConfig.flaskUrl + "/timeSeries";
		ResponseEntity<String> response = createRestTemplate().postForEntity(url, entity, String.class);
        
		return response.getBody();
	}
	
	public RestTemplate createRestTemplate() {
	    // SimpleClientHttpRequestFactory 인스턴스 생성
	    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

	    // BufferingClientHttpRequestFactory로 래핑하여 요청을 버퍼링함
	    BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory(factory);

	    // RestTemplate에 적용
	    RestTemplate restTemplate = new RestTemplate(bufferingFactory);
	    return restTemplate;
	}
    
    public int getUserId(String username) {
    	User user = urepo.findByLoginId(username);
    	return user.toDto().getId();
    }
	
	public void deleteUser(Integer id) {
		urepo.deleteById(id);
	}

	public void deactivateUser(String username) {
		Optional<User> optionalUser = Optional.ofNullable(urepo.findByLoginId(username));
		
		if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setActiveStatus(false);

            // 변경된 필드를 저장
            urepo.save(user);
        } else {
            throw new IllegalArgumentException("Unable to deactivate ; " + username);
        }
	}

	// Test Function
	public String postTest() {
		// 사용자 ID로 사용자 검색
		User user = urepo.findByLoginId("yesSeq");
		
		// 해당 사용자 ID로 PaymentDto 리스트 조회
		List<PaymentDto> paymentList = prepo.findByUser_Id(user.getId()).stream()
				.map(Payment::toDto)
				.collect(Collectors.toList());

        // HttpHeaders 객체를 생성하고 Content-Type을 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 객체를 생성하고 데이터와 헤더를 포함
        HttpEntity<List<PaymentDto>> request = new HttpEntity<>(paymentList, headers);
		
        // Flask API의 URL
        String url = flaskConfig.flaskUrl + "/test";
        
        // POST 요청을 보내고 결과를 String으로 받음
		String response = restTemplate.postForObject(url, request, String.class);
		
		return response;
	}

}
