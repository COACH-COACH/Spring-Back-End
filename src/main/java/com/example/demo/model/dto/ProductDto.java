package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.model.entity.Product;
import com.example.demo.model.enums.DepositCycle;
import com.example.demo.model.enums.ProductType;

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
	private BigDecimal interestRate;
	private BigDecimal maxInterestRate;
	private Date createDate;
	private String preferCondition;
	private String membershipCondition;
	private String eligibility;
	private String caution;
	private String limitAmt;
	private DepositCycle depositCycle;
	private int maturity;
	private ProductType productType;
	
	public Product toEntity() {
		Product product = new Product();
		
		product.setId(this.getId());
		product.setProductName(this.getProductName());
		product.setInterestRate(this.getInterestRate());
		product.setMaxInterestRate(this.getMaxInterestRate());
		product.setCreateDate(this.getCreateDate());
		product.setPreferCondition(this.getPreferCondition());
		product.setMembershipCondition(this.getMembershipCondition());
		product.setEligibility(this.getEligibility());
		product.setCaution(this.getCaution());
		product.setLimitAmt(this.getLimitAmt());
		product.setDepositCycle(this.getDepositCycle());
		product.setMaturity(this.getMaturity());
		product.setProductType(this.getProductType());
		
		return product;
	}
	
}
