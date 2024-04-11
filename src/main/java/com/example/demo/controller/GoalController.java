package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.model.dto.response.GoalSatistics;
import com.example.demo.service.GoalService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.StatusCode;

@RestController
@RequestMapping("/goal")
public class GoalController {
	private final GoalService goalService;
	
	@Autowired
	public GoalController(GoalService goalService) {
		this.goalService = goalService;
	}
	
	@GetMapping("/list")
	public ResponseEntity getGoalList() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<GoalDto> goalList = goalService.getGoalListByUsername(username);
		return new ResponseEntity(DefaultResponse.res(StatusCode.OK,
				ResponseMessage.READ_GOAL_SUCCESS, goalList), HttpStatus.OK);
	}
	
	@GetMapping("/product/list")
	public ResponseEntity<GoalListResDto> getGoalProductList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        GoalListResDto goalListResDto = goalService.getGoalProductListByUsername(username);
        return new ResponseEntity(DefaultResponse.res(StatusCode.OK,
				ResponseMessage.READ_GOAL_SUCCESS, goalListResDto), HttpStatus.OK);
	}
	
	@GetMapping("/statistic")
	public ResponseEntity<GoalSatistics> getGoalDetailList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<GoalSatistics> goalDetailList = goalService.getGoalStatList(username);
        return new ResponseEntity(DefaultResponse.res(StatusCode.OK,
				ResponseMessage.READ_GOAL_SUCCESS, goalDetailList), HttpStatus.OK);
	}

}
