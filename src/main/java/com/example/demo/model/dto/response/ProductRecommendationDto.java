package com.example.demo.model.dto.response;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

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
	@JsonProperty("ID_PK")
	private int idPk;
	
	@JsonProperty("PRODUCT_NAME")
	private String productName;
	
	@JsonProperty("MAX_INTEREST_RATE")
	private int maxInterestRate;
	
	@JsonProperty("MATURITY")
	private int maturity;
	
//	public Integer getId() {
//		return idPk;
//	}
//	public void setId(int idPk) {
//		this.idPk = idPk;
//	}
//	
//	public String getProductName(){
//		return productName;
//	}
//	public void setProductName(String productName) {
//		this.productName = productName;
//	}
//	
//	public int getMaxInterestRate() {
//		return maxInterestRate;
//	}
//	public void setMaxInterestRage(int maxInterestRate) {
//		this.maxInterestRate = maxInterestRate;
//	}
//	
//	public int getMaturity() {
//		return maturity;
//	}
//	public void setMaturity(int maturity) {
//		this.maturity = maturity;
//	}
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
