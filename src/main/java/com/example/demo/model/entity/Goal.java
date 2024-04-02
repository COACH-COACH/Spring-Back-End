package com.example.demo.model.entity;
import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.enums.GoalSt;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="GOAL_TB")
public class Goal {
	@Id
	@Column(name="ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	// 관계 제약 조건 설정 필요 (:M), FK - join column
	@ManyToOne
	@JoinColumn(name="CUSTOMER_ID_FK", referencedColumnName = "ID_PK")
	private User user;
	
	@Column(name="GOAL_NAME")
	private String goalName;
	
	@Column(name="TARGET_COST")
	private BigDecimal targetCost;
	
	@Column(name="ACCUMULATED_BALANCE")
	private BigDecimal accumulatedBalance;
	
	@Column(name="GOAL_ST")
	private GoalSt goalSt;
	
	@Column(name="GOAL_PERIOD")
	private String goalPeriod;
	
	@Column(name="START_DATE")
	private Date startDate;
	
	@Column(name="END_DATE")
	private Date endDate;
	
	// 일대일 관계 - List 형태가 아니라 Enroll 형태
	@OneToOne(mappedBy="goal", cascade = CascadeType.ALL)
	private Enroll enrolls;
	
	// toDto() : entity -> dto
	public GoalDto toDto() {
		GoalDto dto = new GoalDto();
		dto.setId(this.getId());
		dto.setCustomerId(this.getUser().getId());
		dto.setGoalName(this.getGoalName());
		dto.setTargetCost(this.getTargetCost());
		dto.setAccumulatedBalance(this.getAccumulatedBalance());
		dto.setGoalSt(this.getGoalSt());
		dto.setGoalPeriod(this.getGoalPeriod());
		dto.setStartDate(this.getStartDate());
		dto.setEndDate(this.getEndDate());
		return dto;
	}
}