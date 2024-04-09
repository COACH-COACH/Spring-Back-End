package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Goal;

public interface GoalRepo extends JpaRepository<Goal, Integer> {
	List<Goal> findByUserId(int userId);

}
