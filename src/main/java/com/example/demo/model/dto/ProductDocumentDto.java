package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.model.document.ProductDocument;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDocumentDto {
    private String id;
    private String productName;
    private BigDecimal interestRate;
    private BigDecimal maxInterestRate;
    private Date createDate;
    private String preferCondition;
    private String membershipCondition;
    private String eligibility;
    private String caution;
    private String limitAmt;
    private String depositCycle;
    private int maturity;
    private String productType;
    
    public ProductDocument toEntity() {
        return ProductDocument.builder()
            .id(this.getId())
            .productName(this.getProductName())
            .interestRate(this.getInterestRate())
            .maxInterestRate(this.getMaxInterestRate())
            .createDate(this.getCreateDate())
            .preferCondition(this.getPreferCondition())
            .membershipCondition(this.getMembershipCondition())
            .eligibility(this.getEligibility())
            .caution(this.getCaution())
            .limitAmt(this.getLimitAmt())
            .depositCycle(this.getDepositCycle())
            .maturity(this.getMaturity())
            .productType(this.getProductType())
            .build();
    }
}
