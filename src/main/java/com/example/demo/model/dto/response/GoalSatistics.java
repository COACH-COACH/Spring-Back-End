package com.example.demo.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

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
public class GoalSatistics {
	String goalName;
	float goalRate;
	BigDecimal goalAvgTargetAmt;
}
