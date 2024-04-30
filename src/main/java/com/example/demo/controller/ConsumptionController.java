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
	
	// 프론트에서 SEQ와 BAS_YH 필터를 띄워주는 API
	
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
   }
 
