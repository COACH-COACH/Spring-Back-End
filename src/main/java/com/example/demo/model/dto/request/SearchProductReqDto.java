package com.example.demo.model.dto.request;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SearchProductReqDto {
	private Optional<String> productType; // 상품 종류(예금 DEPOSIT, 적금 SAVINGS)
	private Optional<String> depositCycle; // 납입 주기(nullable - 자유적립식 FIXED, 정액적립식 FLEXIBLE, 거치식 HOLD)
	private Integer maturity; // 만기일 (nullable)
	private String keyword; // 검색 키워드
}
