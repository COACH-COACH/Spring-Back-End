package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.model.entity.Enroll;

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
	private BigDecimal accumulatedBalance;
	private Byte maturitySt;
	private String accountNum;
	private BigDecimal depositAmtCycle;
	
	public Enroll toEntity() {
		Enroll enroll = new Enroll();
		enroll.setId(this.getId());
		enroll.setStartDate(this.getStartDate());
		enroll.setEndDate(this.getEndDate());
		enroll.setAccumulatedBalance(this.getAccumulatedBalance());
		enroll.setMaturitySt(this.getMaturitySt());
		enroll.setAccountNum(this.getAccountNum());
		enroll.setDepositAmtCycle(this.getDepositAmtCycle());
		return enroll;
	}
}
