package com.example.demo.service;

import com.example.demo.model.document.NewsDocument;
import com.example.demo.model.dto.response.AdvisorResDto;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.User;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.repository.es.NewsDocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    
    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDate();		
    }
    
    public AdvisorResDto getAdvice(String loginId) {
    	User user = userRepo.findByLoginId(loginId);
	    if (user == null) {
	        throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + loginId);
	    }
    	int userId = user.getId();
        // 사용자의 목표 조회
        List<Goal> goals = goalRepo.findByUserIdAndGoalSt(userId, (byte) 0);
        if (goals.isEmpty()) {
            throw new IllegalStateException("목표를 찾을 수 없어요😥");
        }
        Goal selectedGoal = goals.get(0); // 첫 번째 활성 목표 선택

        String lifeStage = user.getLifeStage().toString();
        List<NewsDocument> newsList = newsDocumentRepo.findByExactKeywords(lifeStage);
        if (newsList.isEmpty()) {
            throw new IllegalStateException("관련 뉴스가 없어요😥");
        }
        
        // 랜덤 뉴스 선택 로직
        Random random = new Random();
        NewsDocument selectedNews = newsList.get(random.nextInt(newsList.size()));
        String selectedNewsTitle = selectedNews.getNewsTitle();
        String selectedNewsDescription = selectedNews.getNewsDescription();
        String selectedNewsUrl = selectedNews.getNewsUrl();
        

        // 진행률 계산
        Date startDate = selectedGoal.getStartDate();
        Date endDate = selectedGoal.getEndDate();
        LocalDate today = LocalDate.now();
        
        // Date type 일치
        LocalDate startLocalDate = convertDateToLocalDate(startDate);
        LocalDate endLocalDate = convertDateToLocalDate(endDate);
        
        long totalDays = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        long elapsedDays = ChronoUnit.DAYS.between(startLocalDate, today);
        double progress = 100.0 * elapsedDays / totalDays;
        String progressPercentage = String.format("%.2f", progress); // 소수점 둘째 자리까지 표시
        
        // 문구 생성
        String newsPrompt = 
        		String.format("%s 생애주기 혹은 목표 %s와 연관지어서 %s 뉴스를 3줄 요약해줘.", 
        				user.getLifeStage(), selectedGoal.getGoalName(), selectedNewsTitle+selectedNewsDescription);
        String goalCheerPrompt = 
        		String.format("현재 목표를 %s 퍼센트 달성한 %s 생애주기인 사람에게 %s 목표와 관련한 동기부여 맞춤 문구 한 마디 생성해줘. 번역 어투는 지양하고, 해요체로 작성해줘.", 
        				progressPercentage, user.getLifeStage(),  selectedGoal.getGoalName());
        String consumptionCheerPrompt =
        		String.format("%s님을 위한 가장 많은 소비를 보인 식비와 관련한 절약 실천방안 문구 한 마디 생성해줘.대안을 제시해줘야해. 번역 어투는 지양하고, 해요체로 작성해줘.", 
        				user.getFullName());
        
        String newsRecommend = extractTextFromJson(geminiService.fetchMessage(newsPrompt));
        String goalCheer = extractTextFromJson(geminiService.fetchMessage(goalCheerPrompt));
        String consumptionCheer = extractTextFromJson(geminiService.fetchMessage(consumptionCheerPrompt));
        
        return new AdvisorResDto(
        	String.format("%s 님을 위한 맞춤 뉴스를 가져왔어요. 제목: %s 요약내용: %s 원문 보기: %s", user.getFullName(), selectedNewsTitle, newsRecommend, selectedNewsUrl),
            String.format("%s 님! \n%s", user.getFullName(), goalCheer),
            String.format("%s 님! \n%s", user.getFullName(), consumptionCheer)
        );
    }
    
    private String extractTextFromJson(String jsonResult) {
    	try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
            if (candidatesArray.length() > 0) {
                JSONObject firstCandidate = candidatesArray.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray partsArray = content.getJSONArray("parts");
                if (partsArray.length() > 0) {
                    JSONObject firstPart = partsArray.getJSONObject(0);
                    return firstPart.getString("text");
                }
            }
            return "텍스트가 없어요."; // Text가 없을 경우 반환할 기본 문자열
        } catch (Exception e) {
            e.printStackTrace();
            return "JSON 처리 중 에러 발생"; // JSON 처리 중 발생한 예외를 처리
        }
    }
}
