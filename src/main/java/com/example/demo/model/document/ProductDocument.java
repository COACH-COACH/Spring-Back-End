package com.example.demo.model.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.example.demo.model.dto.ProductDocumentDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter @Setter
@Document(indexName = "products")
public class ProductDocument {

    @Id
    private String id;

    @Field(name = "PRODUCT_NAME", type = FieldType.Text)
    private String productName;

    @Field(name = "INTEREST_RATE", type = FieldType.Double)
    private BigDecimal interestRate;

    @Field(name = "MAX_INTEREST_RATE", type = FieldType.Double)
    private BigDecimal maxInterestRate;

    @Field(name = "CREATE_DATE", type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd HH:mm:ss.SSSSSS")
    private Date createDate;

    @Field(name = "PREFER_CONDITION", type = FieldType.Text)
    private String preferCondition;

    @Field(name = "MEMBERSHIP_CONDITION", type = FieldType.Text)
    private String membershipCondition;

    @Field(name = "ELIGIBILITY", type = FieldType.Text)
    private String eligibility;

    @Field(name = "CAUTION", type = FieldType.Text)
    private String caution;

    @Field(name = "LIMIT_AMT", type = FieldType.Keyword)
    private String limitAmt;

    @Field(name = "DEPOSIT_CYCLE", type = FieldType.Keyword)
    private String depositCycle;

    @Field(name = "MATURITY", type = FieldType.Integer)
    private int maturity;

    @Field(name = "PRODUCT_TYPE", type = FieldType.Keyword)
    private String productType;
    
    public ProductDocumentDto toDto() {
        return ProductDocumentDto.builder()
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
