package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Enroll;

public interface EnrollRepo extends JpaRepository<Enroll, Integer>{

	Optional<Enroll> findByUserIdAndGoalId(int userId, int goalId);
	Optional<Enroll> findOptionalByUserIdAndGoalId(int userId, int goalId);

}
