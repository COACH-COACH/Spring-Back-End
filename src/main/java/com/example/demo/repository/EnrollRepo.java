package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Enroll;

public interface EnrollRepo extends JpaRepository<Enroll, Integer>{

	Enroll findByUserIdAndGoalId(int userId, int goalId);

}
