package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.entity.Goal;

public interface GoalRepo extends JpaRepository<Goal, Integer> {
	List<Goal> findByUserId(int userId);
	Goal findByUserIdAndId(int userId, int goalId);
	
	List<Goal> findByUserIdAndGoalSt(int userId, Byte goalSt);

	@Query(value = "SELECT GOAL_NAME"
			+ ", COUNT(*) / (SELECT COUNT(*) FROM GOAL_TB) * 100 AS ratio"
			+ ", AVG(TARGET_COST) AS avg_target_cost FROM GOAL_TB GROUP BY GOAL_NAME",
			nativeQuery = true)
    List<Object[]> findGoalStatistics(); // 네이티브 쿼리는 Object[]로 받음 
    
//    @Query("SELECT g FROM Goal g WHERE g.user = :userId AND g.goalSt = 0 AND NOT EXISTS (SELECT 1 FROM Enroll e WHERE e.goal = g.id AND e.user = :userId)")
//    List<Goal> findUnenrolledGoalsByUserId(@Param("userId") int userId);
}
