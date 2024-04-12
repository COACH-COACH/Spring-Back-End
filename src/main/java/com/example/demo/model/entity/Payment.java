package com.example.demo.model.entity;
import java.math.BigDecimal;
//import java.util.List;

import com.example.demo.model.dto.PaymentDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="PAYMENT_TB")
public class Payment {
	@Id
	@Column(name="ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	// FK, 1:M관계
	@ManyToOne
	@JoinColumn(name="CUSTOMER_ID_FK", referencedColumnName = "ID_PK")
	private User user;
	
	@Column(name="CONSUMPTION")
	private BigDecimal consumption;
	
	@Column(name="PAYMENT_TYPE")
	private String paymentType;
	
	@Column(name="PAYMENT_QUARTER")
	private String paymentQuarter;
	
	@Column(name="CATEGORY")
	private String category;
	
	// toDto() : entity -> dto
	public PaymentDto toDto() {
		PaymentDto paymentDto = new PaymentDto();
		paymentDto.setId(this.getId());
		paymentDto.setCustomerId(this.getUser().getId());
		paymentDto.setConsumption(this.getConsumption());
		paymentDto.setPaymentType(this.getPaymentType());
		paymentDto.setPaymentQuarter(this.getPaymentQuarter());
		paymentDto.setCategory(this.getCategory());
		return paymentDto;
	}
}
