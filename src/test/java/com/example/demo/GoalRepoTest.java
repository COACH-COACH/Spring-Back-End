package com.example.demo;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.entity.Plan;
import com.example.demo.repository.PlanRepo;

@SpringBootTest
public class GoalRepoTest {
	
	@Autowired
	PlanRepo planRepo;

	@Test
	void findByEnrollIdTest() {
		Optional<Plan> result = planRepo.findByEnroll_id(16);
	}
}
