package com.example.demo.model.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter @Setter
public class ConnectGoalwithProductResDto {
	private List<GoalListDto> goals;

	@ToString
	@Builder
	@Getter @Setter
	public static class GoalListDto {
		int goalId; // 목표 id
		String goalName; // 목표명
		BigDecimal targetCost; // 목표 금액
		Date startDate; // 시작일
	}
}
