package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.GoalDto;
import com.example.demo.service.GoalService;

@RestController
@RequestMapping("/goal")
public class GoalController {
	private final GoalService goalService;
	
	@Autowired
	public GoalController(GoalService goalService) {
		this.goalService = goalService;
	}
	
	@GetMapping("/list")
	@PreAuthorize("isAuthenticated()")
	public List<GoalDto> getGoalList(Principal principal) {
		String username = principal.getName();
		System.out.println(username);
		List<GoalDto> goalList = goalService.getGoalListByUsername(username);
		return goalList;
	}
}
