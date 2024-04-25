package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Enroll;

public interface EnrollRepo extends JpaRepository<Enroll, Integer>{

	Enroll findByUserIdAndGoalId(int userId, int goalId);
	Optional<Enroll> findOptionalByUserIdAndGoalId(int userId, int goalId);

}
