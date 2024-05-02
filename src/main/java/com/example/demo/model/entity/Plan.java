package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.model.dto.PlanDto;

@Entity
@Builder
@Getter @Setter@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PLAN_TB")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private int id;

    @OneToOne
    @JoinColumn(name = "ENROLL_ID_FK", referencedColumnName = "ID_PK")
    private Enroll enroll;
    
    @Column(name = "ACTION_PLAN")
    private String actionPlan;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "DEPOSIT_AMOUNT")
    private BigDecimal depositAmt;

    @Column(name = "DEPOSIT_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date depositStartDate;

    @Column(name = "DEPOSIT_AMOUNT_CYCLE")
    private Integer depositAmtCycle;

    @Column(name = "TOTAL_COUNT", nullable = false)
    private int totalCount;

    @Column(name = "LAST_DEPOSIT_DATE")
    @Temporal(TemporalType.DATE)
    private Date lastDepositDate;
    
    public PlanDto toDto() {
        PlanDto dto = new PlanDto();
        dto.setId(this.id);
        dto.setEnrollId(this.enroll.getId());
        dto.setActionPlan(this.actionPlan);
        dto.setStartDate(this.startDate);
        dto.setDepositAmt(this.depositAmt);
        dto.setDepositStartDate(this.depositStartDate);
        dto.setDepositAmtCycle(this.depositAmtCycle);
        dto.setTotalCount(this.totalCount);
        dto.setLastDepositDate(this.lastDepositDate);
        return dto;
    }
}