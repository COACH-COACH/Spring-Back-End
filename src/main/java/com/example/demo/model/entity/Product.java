package com.example.demo.model.entity;
import java.math.BigDecimal;
//import java.util.List;

import com.example.demo.model.dto.ProductDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	
	@Column(name="PRODUCT_TYPE")	// 목돈 모으기, 주택청약 등
	private String productType;
	
	@Column(name="ELIGIBILITY")
	private String eligibility;
	
	@Column(name="MIN_TERM")
	private int minTerm;
	
	@Column(name="MAX_TERM")
	private int maxTerm;
	
	@Column(name="TERM_DETAIL")
	private String termDetail;
	
	@Column(name="MIN_AMT")
	private BigDecimal minAmt;
	
	@Column(name="MAX_AMT")
	private BigDecimal maxAmt;
	
	@Column(name="AMT_DETAIL")
	private String amtDetail;
	
	@Column(name="DEPOSIT_CYCLE")
	private String depositCycle;
	
	@Column(name="INTEREST_RATE")
	private BigDecimal interstRate;
	
	@Column(name="PRODUCT_DETAIL")
	private String productDetail;

	// 일대일 관계 - List 형태가 아니라 Enroll 형태
	@OneToOne(mappedBy="product", cascade = CascadeType.ALL)
	private Enroll enrolls;
	
	// toDto() : entity -> dto
	public ProductDto toDto() {
		ProductDto dto = new ProductDto();
		dto.setId(this.getId());
		dto.setProductName(this.getProductName());
		dto.setProductType(this.getProductType());
		dto.setEligibility(this.getEligibility());
		dto.setMinTerm(this.getMinTerm());
		dto.setMaxTerm(this.getMaxTerm());
		dto.setTermDetail(this.getTermDetail());
		dto.setMinAmt(this.getMinAmt());
		dto.setMaxAmt(this.getMaxAmt());
		dto.setAmtDetail(this.getAmtDetail());
		dto.setDepositCycle(this.getDepositCycle());
		dto.setInterstRate(this.getInterstRate());
		dto.setProductDetail(this.getProductDetail());		
		return dto;
	}
}
