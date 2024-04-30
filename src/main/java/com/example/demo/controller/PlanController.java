package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.exception.GoalLimitExceededException;
import com.example.demo.model.dto.PlanDto;
import com.example.demo.model.dto.request.CreatePlanReqDto;
import com.example.demo.service.PlanService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.StatusCode;

@Controller
public class PlanController {

	@Autowired
	private PlanService planService;
	
	@PostMapping
	public ResponseEntity<DefaultResponse<PlanDto>> createPlan(@RequestBody CreatePlanReqDto dto) {
		try {
			PlanDto planDto = planService.savePlan(SecurityUtil.getUsername(), dto);
			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.CREATE_PLAN_SUCCESS, planDto));
		} catch (GoalLimitExceededException e) {
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
	            .body(DefaultResponse.res(StatusCode.SERVICE_UNAVAILABLE, ResponseMessage.CREATE_PLAN_FAIL));
	    }
	}
}
