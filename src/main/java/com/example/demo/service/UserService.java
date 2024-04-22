package com.example.demo.service;
//import java.util.Optional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.PaymentDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Payment;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.model.enums.Sex;
import com.example.demo.repository.PaymentRepo;
import com.example.demo.repository.UserRepo;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Service
public class UserService {
	
	private final UserRepo urepo;
	private final PaymentRepo prepo;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserService(UserRepo urepo, PaymentRepo prepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.urepo = urepo;
        this.prepo = prepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
	
	// 특정 CUSTOMER_ID_FK 값에 해당하는 정보들을 PAYMENT_TB에서 조회
    public List<PaymentDto> getPaymentsByCustomerId(int customerId) {
        List<Payment> payments = prepo.findByUser_Id(customerId);
        return payments.stream()
                       .map(Payment::toDto)
                       .collect(Collectors.toList());
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

}
