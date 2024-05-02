package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.GoalLimitExceededException;
import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.dto.request.CreateGoalReqDto;
import com.example.demo.model.dto.response.GoalCategoryResDto;
import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.service.GoalService;
import com.example.demo.service.UserService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.StatusCode;

@RestController
@RequestMapping("/goal")
public class GoalController {
	private final GoalService goalService;
	private final UserService userService;
	
	@Autowired
	public GoalController(GoalService goalService, UserService userService) {
		this.goalService = goalService;
		this.userService = userService;
	}
	

	// [마이 페이지]
	@GetMapping("/list")
	public ResponseEntity<DefaultResponse<List<GoalDto>>> getGoalList() {
	    String username = SecurityUtil.getUsername();
	    
	    List<GoalDto> goalList = goalService.getGoalListByUsername(username);
	    DefaultResponse<List<GoalDto>> response = DefaultResponse.res(
	        HttpStatus.OK.value(), // 응답 코드
	        ResponseMessage.READ_GOAL_SUCCESS, // 메시지
	        goalList // 데이터
	    );
	    return ResponseEntity.ok(response);
	}
	
	// [메인 페이지] 회원 목표 리스트 조회
	@GetMapping("/product/list")
	public ResponseEntity<DefaultResponse<GoalListResDto>> getGoalProductList() {
	    GoalListResDto goalListResDto = goalService.getGoalProductListByUsername(SecurityUtil.getUsername());
	    DefaultResponse<GoalListResDto> response = DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_GOAL_SUCCESS, goalListResDto);
	    return ResponseEntity.ok(response);
	}

	
	// [목표 생성 페이지] 각 생애주기 별 추천 목표 카테고리 조회
	@GetMapping("/category")
	public ResponseEntity<DefaultResponse<GoalCategoryResDto>> getGoalDetailList() {
		GoalCategoryResDto goalDetailList = goalService.getGoalStatList(SecurityUtil.getUsername());
	    DefaultResponse<GoalCategoryResDto> response = DefaultResponse.res(
	        StatusCode.OK, 
	        ResponseMessage.READ_GOAL_SUCCESS, 
	        goalDetailList
	    );
	    return ResponseEntity.ok(response);
	}
	
	// [목표 생성 페이지] 목표 생성
	@PostMapping("/regist")
	public ResponseEntity<DefaultResponse<GoalDto>> createGoal(@RequestBody CreateGoalReqDto reqDto) {
	    try {
	        GoalDto dto = goalService.addGoal(reqDto, SecurityUtil.getUsername());
	        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.CREATE_GOAL_SUCCESS, dto));
	    } catch (GoalLimitExceededException e) {
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
	            .body(DefaultResponse.res(StatusCode.SERVICE_UNAVAILABLE, ResponseMessage.CREATE_GOAL_FAIL));
	    }
	}
	
	// [메인 페이지] 목표 완료
	@PatchMapping("/complete/{goalId}")
	public ResponseEntity<DefaultResponse<GoalDto>> completeGoal(@PathVariable int goalId) {
		try {
			GoalDto goalDto = goalService.updateGoalState(SecurityUtil.getUsername(), goalId);
	        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPDATE_GOAL_SUCCESS, goalDto));
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(DefaultResponse.res(StatusCode.BAD_REQUEST, e.getMessage()));
	    }
	}
	
	// [메인 페이지] 목표 제거
	@DeleteMapping("/{goalId}")
	public ResponseEntity removeGoal(@PathVariable int goalId) {
		try {
			goalService.deleteGoal(SecurityUtil.getUsername(), goalId);
			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.DELETE_GOAL_SUCCESS));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.DELETE_GOAL_FAIL));
		}
	}

}
