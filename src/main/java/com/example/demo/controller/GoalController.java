package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
	public List<GoalDto> getGoalList() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<GoalDto> goalList = goalService.getGoalListByUsername(username);
		return goalList;
	}
}
