package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.entity.Plan;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.PlanRepo;

@SpringBootTest
public class GoalRepoTest {
	
	@Autowired
	PlanRepo planRepo;
	
	@Autowired
	GoalRepo goalRepo;

	@Test
	void findByEnrollIdTest() {
		Optional<Plan> result = planRepo.findByEnroll_id(16);
	}
	
	@Test
	void 목표별통계량조회() {
		List<Object[]> result = goalRepo.findGoalPercentageByLifeStageAndGoalName("UNI");
		for (Object[] row : result) {
	        System.out.println(Arrays.toString(row)); // 각 행의 내용을 출력
	    }
	}
}
