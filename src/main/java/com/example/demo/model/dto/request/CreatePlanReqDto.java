package com.example.demo.model.dto.request;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanReqDto {
//	public int enrollId; // 가입ID
	public String actionPlan; // 실천방안 내용
//	public Date startDate; // 실천방안 생성일
	public BigDecimal depositAmt; // 입금액
	public Date depositStartDate; // 입금 시작일
	public int depositAmtCycle; // 입금 주기(단위: 일)
//	public int totalCnt; // 입금 횟수
//	public Date lastDepositDate; // 마지막 입금일
}
