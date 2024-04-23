package com.example.demo.model.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class CreateGoalReqDto {
	String goalName; // 목표 카테고리
	BigDecimal targetCost; // 목표 금액(단위: 원)
	int goalPeriod; // 목표 기간(단위: 개월)
}
