package com.example.demo.model.dto.response;


import java.math.BigDecimal;
import java.util.HashMap;
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
public class ProductRecommendationDto {
	private List<ItemRecommendationDto> clusterRecommendations;
	private List<ItemRecommendationDto> itemRecommendations;
	private List<ItemRecommendationDto> staticRecommendations;
	
	// Getter & Setter
	
	// 내부 클래스
	@ToString
	@Builder
	@Getter @Setter
	public static class ItemRecommendationDto{
		private int idPk;
		private int maturity;
		private BigDecimal maxInterestRate;
		private String productName;
		
		public ItemRecommendationDto() {}
		
		public ItemRecommendationDto(int idPk, int maturity, BigDecimal maxInterestRate, String productName) {
			this.idPk = idPk;
			this.maturity = maturity;
			this.maxInterestRate = maxInterestRate;
			this.productName = productName;
		}
	}
}


//	private List<ItemRecommendation> recommendations;
//	

//	public static class ItemRecommendation{
//		private int id;
//		private String productName;
//		private int maxInterestRate;
//		private int maturity;
//	}
//	
//	
//}
