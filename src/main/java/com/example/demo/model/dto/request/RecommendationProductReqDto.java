package com.example.demo.model.dto.request;

import java.math.BigDecimal;
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
public class RecommendationProductReqDto {
//	@NotNull(message = "입금액은 필수입니다.")
//  @Min(value = 1000, message = "입금액은 1000보다 커야 합니다.") -> 프론트에서 제한하기
	private BigDecimal depositAmount;	// 입금액 & 예치금
	private BigDecimal firstDeposit;	// 초기 입금액
}
