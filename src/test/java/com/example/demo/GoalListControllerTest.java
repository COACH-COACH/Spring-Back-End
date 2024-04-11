package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.service.GoalService;

@SpringBootTest
public class GoalListControllerTest {

	@Autowired
	private GoalService goalService;
	
	@Test
	void 목표별상품조회() throws Exception {
		String username = "shin";
		GoalListResDto dto = goalService.getGoalProductListByUsername(username);
		System.out.println(dto.getGoals().toString());
	}
}
