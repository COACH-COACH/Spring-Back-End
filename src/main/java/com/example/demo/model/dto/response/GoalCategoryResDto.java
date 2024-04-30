package com.example.demo.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.enums.LifeStage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GoalCategoryResDto {
	public String fullName;
	public List<GoalStatistics> categoryList;

	@ToString
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter @Setter
	public static class GoalStatistics {
		String goalName;
		float goalRate;
		BigDecimal goalAvgTargetAmt;
	}
}
