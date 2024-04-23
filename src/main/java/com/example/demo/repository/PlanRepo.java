package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Plan;

public interface PlanRepo extends JpaRepository<Plan, Integer>{
	Optional<Plan> findByEnroll_id(int enrollId);
}
