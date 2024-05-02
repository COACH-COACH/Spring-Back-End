package com.example.demo.model.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanResDto {
	// User
	public String fullName;
	
	// Goal
	public String goalName;
	
	// Enroll
	public BigDecimal targetAmt; // 목표 금액
	public BigDecimal remainAmt; // 잔여 금액(목표금액 - 현재금액)
	public int remainDay; // 잔여 기간(가입 종료 기간 - 현재 기간)
	
	public BigDecimal monthlyReqAmt; // 월별 필요 금액
	public BigDecimal weeklyReqAmt; // 주별 필요 금액
	public BigDecimal dailyReqAmt; // 일별 필요 금액
	
	// 고정
	public List<String> actionList; // 소비패턴 별 실천방안
}
