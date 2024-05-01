package com.example.demo.model.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.example.demo.model.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GoalListResDto {
	int userId; // 유저 ID
	String fullName; // 유저 이름
	List<GoalAndProductDto> goals;
	
	@ToString
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter @Setter
	public static class GoalAndProductDto {
		// 목표 관련 필드 
		int goalId; // 목표 id
		String goalName; // 목표명
		Byte goalSt; // 목표 달성 상태
		Date goalStartDate; // 목표 시작일
		
		// 등록 관련 필드
		int enrollId; // 상품 id
		Date productStartDate; // 상품 가입일
		BigDecimal accumulatedBalance; // 상품 누적 금액
		String accountNum; // 계좌번호
		float goalRate; // 달성율(단위: %)
		BigDecimal targetCost; // 목표금액
		
		// 실천방안 관련 필드(자유적금일 경우)
		String actionPlan; // 실천 방안
		BigDecimal depositAmt; // 주기 별 입금 금액
		Date depositStartDate; // 실천 방안 입금 시작일
		Integer depositAmtCycle; // 주기
		int totalCount; // 입금 횟수
		Date lastDepositDate; // 마지막 입금일
		
		// 상품 관련 필드 
		int productId;// 상품 가입 번호
		String productName; // 상품명(PRODUCT_TB)
		String depositCycle; //상품 종류
		
		// 통계
		GoalStatistics goalStat; // 통계량 변수 모음 객체
	}
	
	@ToString
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter @Setter
	public static class GoalStatistics {
		public float proportion; // 생애주기 별 전체 참여 목표에서 차지하는 비율 
		public BigDecimal avgAmt; // 평균 모인 금액
	}
}
