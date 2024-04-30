package com.example.demo.model.document;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "consumption-original")
public class ConsumptionDocument {
	@Id
	private String id;
	
	@Field(type = FieldType.Keyword)
	private String seq;
	
	@Field(type=FieldType.Text)
	private String basYh;
	
	@Field(type=FieldType.Integer)
	private int funitrAm;
	
	@Field(type=FieldType.Integer)
	private int applncAm;
	
	@Field(type=FieldType.Integer)
	private int hlthfsAm;
	
	@Field(type=FieldType.Integer)
	private int bldmngAm;

	@Field(type=FieldType.Integer)
	private int architAm;

	@Field(type=FieldType.Integer)
	private int opticAm;

	@Field(type=FieldType.Integer)
	private int agrictrAm;

	@Field(type=FieldType.Integer)
	private int leisureSAm;

	@Field(type=FieldType.Integer)
	private int leisurePAm;

	@Field(type=FieldType.Integer)
	private int cultureAm;

	@Field(type=FieldType.Integer)
	private int sanitAm;

	@Field(type=FieldType.Integer)
	private int insuAm;

	@Field(type=FieldType.Integer)
	private int offcomAm;

	@Field(type=FieldType.Integer)
	private int bookAm;

	@Field(type=FieldType.Integer)
	private int rprAm;

	@Field(type=FieldType.Integer)
	private int hotelAm;

	@Field(type=FieldType.Integer)
	private int goodsAm;

	@Field(type=FieldType.Integer)
	private int trvlAm;

	@Field(type=FieldType.Integer)
	private int fuelAm;

	@Field(type=FieldType.Integer)
	private int svcAm;

	@Field(type=FieldType.Integer)
	private int distbnpAm;

	@Field(type=FieldType.Integer)
	private int distbpAm;

	@Field(type=FieldType.Integer)
	private int groceryAm;

	@Field(type=FieldType.Integer)
	private int hosAm;

	@Field(type=FieldType.Integer)
	private int clothAm;

	@Field(type=FieldType.Integer)
	private int restrntAm;

	@Field(type=FieldType.Integer)
	private int automntAm;

	@Field(type=FieldType.Integer)
	private int autoslAm;

	@Field(type=FieldType.Integer)
	private int kitwrAm;

	@Field(type=FieldType.Integer)
	private int fabricAm;

	@Field(type=FieldType.Integer)
	private int acdmAm;

	@Field(type=FieldType.Integer)
	private int mbrshopAm;

    public ConsumptionDocument toDto() {
        return ConsumptionDocument.builder()
                .seq(this.getSeq())
                .bldmngAm(this.getBldmngAm())
                .architAm(this.getArchitAm())
                .opticAm(this.getOpticAm())
                .agrictrAm(this.getAgrictrAm())
                .leisureSAm(this.getLeisureSAm())
                .leisurePAm(this.getLeisurePAm())
                .cultureAm(this.getCultureAm())
                .sanitAm(this.getSanitAm())
                .insuAm(this.getInsuAm())
                .offcomAm(this.getOffcomAm())
                .bookAm(this.getBookAm())
                .rprAm(this.getRprAm())
                .hotelAm(this.getHotelAm())
                .goodsAm(this.getGoodsAm())
                .trvlAm(this.getTrvlAm())
                .fuelAm(this.getFuelAm())
                .svcAm(this.getSvcAm())
                .distbnpAm(this.getDistbnpAm())
                .distbpAm(this.getDistbpAm())
                .groceryAm(this.getGroceryAm())
                .hosAm(this.getHosAm())
                .clothAm(this.getClothAm())
                .restrntAm(this.getRestrntAm())
                .automntAm(this.getAutomntAm())
                .autoslAm(this.getAutoslAm())
                .kitwrAm(this.getKitwrAm())
                .fabricAm(this.getFabricAm())
                .acdmAm(this.getAcdmAm())
                .mbrshopAm(this.getMbrshopAm())
                .build();
   }
}
