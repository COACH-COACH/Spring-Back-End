package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.model.entity.Enroll;
import com.example.demo.model.enums.MaturitySt;

//import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollDto {
	private int id;
	private int customerId;
	private int productId;
	private int goalId;
	private Date startDate;
	private Date endDate;
	private BigDecimal regularDeposit;
	private BigDecimal accumulatedBalance;
	private MaturitySt maturitySt;
	
	public Enroll toEntity() {
		Enroll enroll = new Enroll();
		enroll.setId(this.getId());
		enroll.setStartDate(this.getStartDate());
		enroll.setEndDate(this.getEndDate());
		enroll.setRegularDeposit(this.getRegularDeposit());
		enroll.setAccumulatedBalance(this.getAccumulatedBalance());
		enroll.setMaturitySt(this.getMaturitySt());
		return enroll;
	}
}