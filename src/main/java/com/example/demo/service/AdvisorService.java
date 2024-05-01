package com.example.demo.service;

import com.example.demo.model.dto.response.AdvisorResDto;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.User;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.repository.es.NewsDocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AdvisorService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GoalRepo goalRepo;

    @Autowired
    private NewsDocumentRepo newsDocumentRepo;

    @Autowired
    private GeminiService geminiService;
    public AdvisorResDto getAdvice(String loginId) {
    	User user = userRepo.findByLoginId(loginId);
	    if (user == null) {
	        throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + loginId);
	    }
    	int userId = user.getId();
        // 사용자의 목표 조회
        List<Goal> goals = goalRepo.findByUserIdAndGoalSt(userId, (byte) 0);
        if (goals.isEmpty()) {
            throw new IllegalStateException("No active goals found for user.");
        }
        Goal selectedGoal = goals.get(0); // 첫 번째 활성 목표 선택


        // 뉴스 선택 로직
        String lifeStage = user.getLifeStage().toString();
        var newsList = newsDocumentRepo.findByExactKeywords(lifeStage);
        if (newsList.isEmpty()) {
            throw new IllegalStateException("관련 뉴스가 없어요😥");
        }
        String selectedNewsTitle = newsList.get(new Random().nextInt(newsList.size())).getNewsTitle();
        
        // 동기 부여 문구 생성
        String goalCheer = "Keep pushing towards your goal!";
        String consumptionCheer = "Consider revising your budget to save more!";
        
        return new AdvisorResDto(
            String.format("%s님을 위한 오늘의 %s 목표 관련 기사예요!\n %s", user.getFullName(), selectedGoal.getGoalName(), selectedNewsTitle),
            goalCheer,
            consumptionCheer
        );
    }
}
