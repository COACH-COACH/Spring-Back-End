package com.example.demo.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.PlanDto;
import com.example.demo.model.dto.request.CreatePlanReqDto;
import com.example.demo.model.entity.Plan;
import com.example.demo.model.entity.User;
import com.example.demo.repository.EnrollRepo;
import com.example.demo.repository.PlanRepo;
import com.example.demo.repository.UserRepo;

@Service
public class PlanService {
	
	@Autowired
	private PlanRepo planRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private EnrollRepo enrollRepo;

	public PlanDto savePlan(String username, CreatePlanReqDto dto) {
		User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        	new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
		
		Plan newPlan = new Plan().builder()
				.enroll(enrollRepo.findById(dto.getEnrollId()).get())
				.actionPlan(dto.actionPlan)
				.startDate(new Date())
				.depositAmt(dto.depositAmt)
				.depositStartDate(dto.depositStartDate)
				.depositAmtCycle(dto.depositAmtCycle)
				.totalCount(0)
				.build();
		
		return planRepo.save(newPlan).toDto();
	}
}
