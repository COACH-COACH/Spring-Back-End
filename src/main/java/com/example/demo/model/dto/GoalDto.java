package com.example.demo.model.dto;
import java.math.BigDecimal;
import java.util.Date;

//import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.entity.Goal;
import com.example.demo.model.enums.GoalSt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalDto {
	private int id;
	private int customerId;
	private String goalName;
	private BigDecimal targetCost;
	private BigDecimal accumulatedBalance;
	private GoalSt goalSt;
	private String goalPeriod;
	private Date startDate;
	private Date endDate;
	
	public Goal toEntity() {
		Goal goal = new Goal();
		goal.setId(this.getId());
		goal.setGoalName(this.getGoalName());
		goal.setTargetCost(this.getTargetCost());
		goal.setAccumulatedBalance(this.getAccumulatedBalance());
		goal.setGoalSt(this.getGoalSt());
		goal.setGoalPeriod(this.getGoalPeriod());
		goal.setStartDate(this.getStartDate());
		goal.setEndDate(this.getEndDate());
		return goal;
	}
}
