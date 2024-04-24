package com.example.demo.controller;

import com.example.demo.service.NewsService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.dto.response.NewsResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/{loginId}")
    public List<NewsResDto> getNewsByUserPreferences(@PathVariable String loginId) {
        return newsService.findNewsByUserPreferences(loginId);
    }
    
	// Test API
	@GetMapping("/list")
	public ResponseEntity<DefaultResponse<List<NewsResDto>>> getNewsList() {
	    String username = SecurityUtil.getUsername();
	    List<NewsResDto> newsList = newsService.findNewsByUserPreferences(username);
	    DefaultResponse<List<NewsResDto>> response = DefaultResponse.res(
	        HttpStatus.OK.value(), // 응답 코드
	        ResponseMessage.READ_NEWS_SUCCESS, // 메시지
	        newsList // 데이터
	    );
	   
	    return ResponseEntity.ok(response);
	}
}
