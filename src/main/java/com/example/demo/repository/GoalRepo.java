package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.entity.Goal;
import com.example.demo.model.enums.LifeStage;

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
    
    // 생애주기 별 목표 선택 비율
//    @Query(value = "SELECT u.LIFE_STAGE, g.GOAL_NAME, COUNT(g.GOAL_NAME) * 100.0 / SUM(COUNT(g.GOAL_NAME)) OVER() AS percentage "
//            + "FROM USER_TB u "
//            + "JOIN GOAL_TB g ON u.ID_PK = g.USER_ID_FK "
//            + "WHERE u.LIFE_STAGE = :lifeStage AND g.GOAL_ST = 0 "
//            + "GROUP BY g.GOAL_NAME", 
//            nativeQuery = true)
//    List<Object[]> findGoalPercentageByLifeStageAndGoalName(@Param("lifeStage") String lifeStage);
    
    @Query(value = "SELECT " +
            "u.LIFE_STAGE, " +
            "g.GOAL_NAME, " +
            "COUNT(g.GOAL_NAME) * 100.0 / SUM(COUNT(g.GOAL_NAME)) OVER() AS percentage, " +
            "IFNULL(AVG(e.ACCUMULATED_BALANCE), 0) AS avg_accumulated_balance " +
            "FROM USER_TB u " +
            "JOIN GOAL_TB g ON u.ID_PK = g.USER_ID_FK " +
            "LEFT JOIN ENROLL_TB e ON g.ID_PK = e.GOAL_ID_FK " +
            "WHERE u.LIFE_STAGE = :lifeStage AND g.GOAL_ST = 0 " +
            "GROUP BY g.GOAL_NAME",
    nativeQuery = true)
List<Object[]> findGoalPercentageByLifeStageAndGoalName(@Param("lifeStage") String lifeStage);

}
