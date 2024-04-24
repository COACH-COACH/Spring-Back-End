package com.example.demo.service;

import com.example.demo.model.document.NewsDocument;
import com.example.demo.model.dto.response.NewsResDto;
import com.example.demo.repository.es.NewsDocumentRepo;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.model.entity.Goal;
import com.example.demo.repository.UserRepo;
import com.example.demo.repository.GoalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsDocumentRepo newsDocumentRepo;
    private final UserRepo userRepo;
    private final GoalRepo goalRepo;

    @Autowired
    public NewsService(NewsDocumentRepo newsDocumentRepo, UserRepo userRepo, GoalRepo goalRepo) {
        this.newsDocumentRepo = newsDocumentRepo;
        this.userRepo = userRepo;
        this.goalRepo = goalRepo;
    }

    public List<NewsResDto> findNewsByUserPreferences(String loginId) {
        User user = userRepo.findByLoginId(loginId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + loginId);
        }
        LifeStage lifeStage = user.getLifeStage();
        List<Goal> goals = goalRepo.findByUserId(user.getId());
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }

        List<NewsResDto> newsResDtoList = new ArrayList<>();
        for (Goal goal : goals) {
            String searchKeyword = lifeStage + " - " + goal.getGoalName();
            List<NewsDocument> newsDocuments = newsDocumentRepo.findByExactKeywords(searchKeyword);

            if (newsDocuments != null) {
                newsDocuments.stream()
                    .distinct()
                    .map(this::convertToNewsResponse)
                    .forEach(newsResDtoList::add);
            }
        }
        return newsResDtoList;
    }

    private NewsResDto convertToNewsResponse(NewsDocument newsDocument) {
        return NewsResDto.builder()
                .newsTitle(newsDocument.getNewsTitle())
                .newsDate(newsDocument.getNewsDate())
                .newsDescription(newsDocument.getNewsDescription())
                .newsUrl(newsDocument.getNewsUrl())
                .newsKeywords(newsDocument.getNewsKeywords())
                .build();
    }
}
