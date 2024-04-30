package com.example.demo.controller;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ConsumptionService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/consumption")
public class ConsumptionController {
	private final ConsumptionService consumptionService;
	private final UserService userService;
	
	@Autowired
    public ConsumptionController(ConsumptionService consumptionService, UserService userService) {
        this.consumptionService = consumptionService;
        this.userService = userService;
    }	
	
	// 프론트에서 SEQ와 BAS_YH 컨트롤러를 띄워주는 API
	
	
	// elasticSearch 집계 받아옴
    @GetMapping("/max")
    public ResponseEntity<?> getQuarterData() {
        String username = SecurityUtil.getUsername();
        int userId = userService.getUserId(username);
        String seq = userService.getUser(userId).getSeq();
        
        try {
            Map<String, Object> data = consumptionService.aggregateTotalSpendingBySeq(seq);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
    
    // 직전분기와 현재분기 비교 통계량
    @GetMapping("/compare")
    public ResponseEntity<?> compareQuarters(@RequestParam String quarter) {
        String username = SecurityUtil.getUsername();
        int userId = userService.getUserId(username);
        String seq = userService.getUser(userId).getSeq();
	    
        String currentQuarter = quarter;
	    String previousQuarter = consumptionService.calculatePreviousQuarter(quarter);
	    
	    if (previousQuarter.contains("2021")) {
	    	return ResponseEntity.internalServerError().body("직전 분기가 존재하지 않습니다.");
	    }
	    
        try {
            Map<String, Map<String, Integer>> data = consumptionService.calculateQuarterDifference(seq, currentQuarter, previousQuarter);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }

    }

   }
 
