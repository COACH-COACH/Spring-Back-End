package com.example.demo.model.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
		BigDecimal targetCost; // 목표 금액
		Byte goalSt; // 목표 달성 상태
		Date startDate; // 시작일
		int goalPeriod; // 기간(단위:개월)
		BigDecimal totalBalance; // 목표 누적 금액
		
		// 등록 관련 필드
		int enrollId; // 상품 id
		BigDecimal accumulatedBalance; // 상품 누적 금액
		String accountNum; // 계좌번호
		float goalRate; // 달성율(단위: %)
		
		// 상품 관련 필드 
		int productId;// 상품 가입 번호
		String productName; // 상품명(PRODUCT_TB)
		
	}
}
