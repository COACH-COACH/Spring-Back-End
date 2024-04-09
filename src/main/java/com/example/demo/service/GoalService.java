package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.User;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.UserRepo;

@Service
public class GoalService {
	private GoalRepo goalRepo;
	private UserRepo userRepo;
	
	@Autowired
	public GoalService(GoalRepo goalRepo, UserRepo userRepo) {
		this.goalRepo = goalRepo;
		this.userRepo = userRepo;
	}
	
	public List<GoalDto> getGoalListByUsername(String username) {
		User user = userRepo.findByLoginId(username);
		if (user == null) {
	        throw new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니: " + username);
	    }
		// 해당 사용자의 목표 목록 조회
	    List<Goal> goals = goalRepo.findByUserId(user.getId());
		return goals.stream().map(Goal::toDto).collect(Collectors.toList());
	}
}
