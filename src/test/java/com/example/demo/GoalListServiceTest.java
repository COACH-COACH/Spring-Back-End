package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.model.dto.response.GoalStatisticsResDto;
import com.example.demo.service.GoalService;

@SpringBootTest
public class GoalListServiceTest {

	@Autowired
	private GoalService goalService;
	
	@Test
	void 목표별상품조회() throws Exception {
		String username = "shin";
		GoalListResDto dto = goalService.getGoalProductListByUsername(username);
		System.out.println(dto.getGoals().toString());
	}
	
	@Test
	void 생명주기별목표조회() {
		String username = "shin";
		List<GoalStatisticsResDto> goalDetailList = goalService.getGoalStatList(username);
		for (GoalStatisticsResDto goalDetail: goalDetailList) {
			System.out.println(goalDetail.toString());
		}
	}
	
	@Test
	void 목표상태변경() {
		String username = "shin";
		int goalId = 2;
		GoalDto dto;
		try {
			dto = goalService.updateGoalState(username, goalId);
			System.out.println(dto.toString());
		} catch (Exception e) {
			System.out.println("이미 완료된 목표입니다. ");
		}
	}
}
