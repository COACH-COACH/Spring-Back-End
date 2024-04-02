package com.example.demo.model.dto;

import java.math.BigDecimal;

import com.example.demo.model.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
	private int id;
	private int customerId;
	private BigDecimal consumption;
	private String paymentType;
	private String paymentQuarter;
	private String category;
	
	public Payment toEntity() {
		Payment payment = new Payment();
		payment.setId(this.getId());
		payment.setConsumption(this.getConsumption());
		payment.setPaymentType(this.getPaymentType());
		payment.setPaymentQuarter(this.getPaymentQuarter());
		payment.setCategory(this.getCategory());
		return payment;
	}
}
