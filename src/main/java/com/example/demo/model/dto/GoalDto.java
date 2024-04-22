package com.example.demo.model.dto;
import java.math.BigDecimal;
import java.util.Date;

//import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.entity.Goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalDto {
	private int id;
	private int userId;
	private String goalName;
	private BigDecimal accumulatedBalance;
	private Byte goalSt;
	private Date startDate;
	private Date endDate;
	
	public Goal toEntity() {
		Goal goal = new Goal();
		goal.setId(this.getId());
		goal.setGoalName(this.getGoalName());
		goal.setAccumulatedBalance(this.getAccumulatedBalance());
		goal.setGoalSt(this.getGoalSt());
		goal.setStartDate(this.getStartDate());
		goal.setEndDate(this.getEndDate());
		return goal;
	}
}
