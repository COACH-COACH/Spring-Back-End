package com.example.demo.model.entity;
import java.math.BigDecimal;
//import java.util.List;
import java.util.Date;

import com.example.demo.model.dto.EnrollDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(of = {"id"})
@Builder
@Data   // RequiredArgsConstructor만 가져오기 때문 
@NoArgsConstructor
@AllArgsConstructor
@Entity // h2 database에 user 라는 테이블을 만들었더니 오류 - mysql은 상관없음
@Table(name="ENROLL_TB")
public class Enroll {
	@Id
	@Column(name="ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	// 관계 제약 조건 설정 필요 (1:M), FK - join column
	@ManyToOne
	@JoinColumn(name="USER_ID_FK", referencedColumnName = "ID_PK")
	private User user;
	
	// 관계 제약 조건 설정 필요(1:1), FK - join column
	@OneToOne
	@JoinColumn(name="PRODUCT_ID_FK", referencedColumnName = "ID_PK")
	private Product product;
	
	// 관계 제약 조건 설정 필요(1:1), FK - join column
	@OneToOne
	@JoinColumn(name="GOAL_ID_FK", referencedColumnName="ID_PK")
	private Goal goal;
	
	@Column(name="START_DATE")
	private Date startDate;
	
	@Column(name="END_DATE")
	private Date endDate;
	
	@Column(name="ACCUMULATED_BALANCE")
	private BigDecimal accumulatedBalance;
	
	@Column(name="MATURITY_ST")
	private Byte maturitySt;
	
	// toDto() : entity -> dto
	public EnrollDto toDto() {
		EnrollDto dto = new EnrollDto();
		dto.setId(this.getId());
		dto.setCustomerId(this.getUser().getId());
		dto.setProductId(this.getProduct().getId());
		dto.setGoalId(this.getGoal().getId());
		dto.setStartDate(this.getStartDate());
		dto.setEndDate(this.getEndDate());
		dto.setAccumulatedBalance(this.getAccumulatedBalance());
		dto.setMaturitySt(this.getMaturitySt());
		return dto;
	}
}
