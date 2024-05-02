package com.example.demo.controller;

import com.example.demo.service.NewsService;
import com.example.demo.service.UserService;
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
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;
    private final UserService userService;

    @Autowired
    public NewsController(NewsService newsService, UserService userService) {
        this.newsService = newsService;
        this.userService = userService; 
    }

	@GetMapping("/list")
	public ResponseEntity<DefaultResponse<List<NewsResDto>>> getNewsList() {
	    String username = SecurityUtil.getUsername();
	    Integer userId = userService.getUserId(username);
	    List<NewsResDto> newsList = newsService.findNewsByUserPreferences(userId);
	    DefaultResponse<List<NewsResDto>> response = DefaultResponse.res(
	        HttpStatus.OK.value(), // 응답 코드
	        ResponseMessage.READ_NEWS_SUCCESS, // 메시지
	        newsList // 데이터
	    );
	   
	    return ResponseEntity.ok(response);
	}
}
