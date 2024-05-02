package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.PlanDto;
import com.example.demo.model.dto.request.CreatePlanReqDto;
import com.example.demo.model.dto.response.CreatePlanResDto;
import com.example.demo.model.entity.Enroll;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.Plan;
import com.example.demo.model.entity.User;
import com.example.demo.repository.EnrollRepo;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.PlanRepo;
import com.example.demo.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class PlanService {
	
	@Autowired
	private PlanRepo planRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private EnrollRepo enrollRepo;
	
	@Autowired
	private GoalRepo goalRepo;

	@Transactional
	public PlanDto savePlan(String username, CreatePlanReqDto dto, int enrollId) throws Exception {
		User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        	new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
		
		Optional<Enroll> res = enrollRepo.findById(enrollId);
		if (!res.isPresent()) {
			throw new Exception("존재하지 않는 목표입니다.");
		}
		Enroll enroll = res.get();
		System.out.println(enroll.toString());
		
		if (planRepo.findByEnroll_id(enrollId).isPresent()) {
			throw new Exception("이미 실천방안이 추가된 목표입니다.");
		}
		
		Plan newPlan = new Plan().builder()
				.enroll(enroll)
				.actionPlan(dto.actionPlan)
				.startDate(new Date())
				.depositAmt(dto.depositAmt)
				.depositStartDate(dto.depositStartDate)
				.depositAmtCycle(dto.depositAmtCycle)
				.totalCount(0)
				.build();
		
		return planRepo.save(newPlan).toDto();
	}

	public CreatePlanResDto findActionPlanDetail(String username, int enrollId) throws Exception {
		User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
    		new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
		
		Optional<Enroll> resEnroll = enrollRepo.findById(enrollId);
		if (resEnroll.isEmpty()) {
			throw new Exception("존재하지 않는 상품입니다.");
		}
		Enroll enroll = resEnroll.get();
		
		Optional<Goal> resGoal = goalRepo.findById(enroll.getGoal().getId());
		if (resGoal.isEmpty()) {
			throw new Exception("존재하지 않는 목표입니다.");
		}
		Goal goal = resGoal.get();
		
		BigDecimal remainAmt = enroll.getTargetCost().subtract(enroll.getAccumulatedBalance()); // 남은 금액
		int remainDay = calRemainDay(enroll.getEndDate()); // 남은 일수
		
		CreatePlanResDto resDto = new CreatePlanResDto().builder()
				.fullName(user.getFullName())
				.goalName(goal.getGoalName())
				.targetAmt(enroll.getTargetCost())
				.remainAmt(remainAmt)
				.remainDay(remainDay)
				.monthlyReqAmt(remainAmt.divide(new BigDecimal(remainDay / 30), RoundingMode.HALF_UP))
				.weeklyReqAmt(remainAmt.divide(new BigDecimal(remainDay / 7), RoundingMode.HALF_UP))
				.dailyReqAmt(remainAmt.divide(new BigDecimal(remainDay), RoundingMode.HALF_UP))
				.actionList(getActionPlanOfConsumption())
				.build();
		return resDto;
	}
	
	public int calRemainDay(Date endDate) {
		Date today = new Date();
	    long diffInMillies = endDate.getTime() - today.getTime();
	    long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    if (days > Integer.MAX_VALUE) {
	        throw new ArithmeticException("The number of days is too large to fit in an int.");
	    } else if (days < Integer.MIN_VALUE) {
	        throw new ArithmeticException("The number of days is too small to fit in an int.");
	    }
	    return (int) days;
	}
	
	private List<String> getActionPlanOfConsumption() {
		List<String> actionPlanList = Arrays.asList("커피 줄이기", "외식 줄이기", "배달 줄이기", "편의점 줄이기", "쇼핑 줄이기");
		return actionPlanList;
	}
}
