package com.example.demo.model.dto;

import java.math.BigDecimal;

import com.example.demo.model.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
	private int id;
	private String productName;
	private String productType;
	private String eligibility;
	private int minTerm;
	private int maxTerm;
	private String termDetail;
	private BigDecimal minAmt;
	private BigDecimal maxAmt;
	private String amtDetail;
	private String depositCycle;
	private BigDecimal interstRate;
	
	public Product toEntity() {
		Product product = new Product();
		product.setId(this.getId());
		product.setProductName(this.getProductName());
		product.setProductType(this.getProductType());
		product.setEligibility(this.getEligibility());
		product.setMinTerm(this.getMinTerm());
		product.setMaxTerm(this.getMaxTerm());
		product.setTermDetail(this.getTermDetail());
		product.setMinAmt(this.getMinAmt());
		product.setMaxAmt(this.getMaxAmt());
		product.setAmtDetail(this.getAmtDetail());
		product.setDepositCycle(this.getDepositCycle());
		product.setInterstRate(this.getInterstRate());
		return product;
	}
	
}
