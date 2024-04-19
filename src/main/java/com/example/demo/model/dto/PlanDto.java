package com.example.demo.model.dto;

import com.example.demo.model.entity.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanDto {
    private int id;
    private int enrollId;
    private String actionPlan;
    private Date startDate;
    private BigDecimal depositAmt;
    private Date depositStartDate;
    private Integer depositAmtCycle;
    private int totalCount;
    private Date lastDepositDate;

    public Plan toEntity() {
        Plan plan = new Plan();
        plan.setId(this.id);
        plan.setActionPlan(this.actionPlan);
        plan.setStartDate(this.startDate);
        plan.setDepositAmt(this.depositAmt);
        plan.setDepositStartDate(this.depositStartDate);
        plan.setDepositAmtCycle(this.depositAmtCycle);
        plan.setTotalCount(this.totalCount);
        plan.setLastDepositDate(this.lastDepositDate);
        return plan;
    }
}
