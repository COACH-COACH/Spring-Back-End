package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.exception.GoalLimitExceededException;
import com.example.demo.model.dto.PlanDto;
import com.example.demo.model.dto.request.CreatePlanReqDto;
import com.example.demo.model.dto.response.CreatePlanResDto;
import com.example.demo.service.PlanService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.StatusCode;

@Controller
@RequestMapping("/plan")
public class PlanController {

	@Autowired
	private PlanService planService;
	
	@PostMapping("/{enrollId}")
	public ResponseEntity<DefaultResponse<PlanDto>> createPlan(@RequestBody CreatePlanReqDto dto, @PathVariable int enrollId) {
		System.out.println(dto.toString());
		try {
			PlanDto planDto = planService.savePlan(SecurityUtil.getUsername(), dto, enrollId);
			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.CREATE_PLAN_SUCCESS, planDto));
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
	            .body(DefaultResponse.res(StatusCode.SERVICE_UNAVAILABLE, e.getMessage()));
	    }
	}
	
	@GetMapping("/{enrollId}")
	public ResponseEntity<DefaultResponse<CreatePlanResDto>> getActionPlanDetail(@PathVariable int enrollId) {
		try {
			CreatePlanResDto resDto = planService.findActionPlanDetail(SecurityUtil.getUsername(), enrollId);
			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_PLAN_SUCCESS, resDto));
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
		            .body(DefaultResponse.res(StatusCode.SERVICE_UNAVAILABLE, e.getMessage()));
		}
		
		
	}
}
