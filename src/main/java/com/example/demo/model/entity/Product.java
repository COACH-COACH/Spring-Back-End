package com.example.demo.model.entity;
import java.math.BigDecimal;
//import java.util.List;
import java.util.Date;

import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.enums.DepositCycle;
import com.example.demo.model.enums.ProductType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
//import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import lombok.ToString;

@Builder
@Data   // RequiredArgsConstructor만 가져오기 때문 
@NoArgsConstructor
@AllArgsConstructor
@Entity // h2 database에 user 라는 테이블을 만들었더니 오류 - mysql은 상관없음
@Table(name="PRODUCT_TB")
public class Product {
	@Id
	@Column(name="ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="PRODUCT_NAME")
	private String productName;
	
	@Column(name="INTEREST_RATE")
	private BigDecimal interestRate;
	
	@Column(name="MAX_INTEREST_RATE")
	private BigDecimal maxInterestRate;
	
	@Column(name="CREATE_DATE")
	private Date createDate;
	
	@Column(name="PREFER_CONDITION")
	private String preferCondition;
	
	@Column(name="MEMBERSHIP_CONDITION")
	private String membershipCondition;
	
	@Column(name="ELIGIBILITY")
	private String eligibility;
	
	@Column(name="CAUTION")
	private String caution;
	
	@Column(name="LIMIT_AMT")
	private String limitAmt;
	
	@Column(name="DEPOSIT_CYCLE")
	@Enumerated(EnumType.STRING)
	private DepositCycle depositCycle;
	
	@Column(name="MATURITY")
	private int maturity;
	
	@Column(name="PRODUCT_TYPE")
	@Enumerated(EnumType.STRING) // 목돈 모으기, 주택청약 등
	private ProductType productType;

	// 일대일 관계 - List 형태가 아니라 Enroll 형태
	@OneToOne(mappedBy="product", cascade = CascadeType.ALL)
	private Enroll enrolls;
	
	// toDto() : entity -> dto
	public ProductDto toDto() {
		ProductDto dto = new ProductDto();
		
		dto.setId(this.getId());
		dto.setProductName(this.getProductName());
		dto.setInterestRate(this.getInterestRate());
		dto.setMaxInterestRate(this.getMaxInterestRate());
		dto.setCreateDate(this.getCreateDate());
		dto.setPreferCondition(this.getPreferCondition());
		dto.setMembershipCondition(this.getMembershipCondition());
		dto.setEligibility(this.getEligibility());
		dto.setCaution(this.getCaution());
		dto.setLimitAmt(this.getLimitAmt());
		dto.setDepositCycle(this.getDepositCycle());
		dto.setMaturity(this.getMaturity());
		dto.setProductType(this.getProductType());	
		
		return dto;
	}
}
