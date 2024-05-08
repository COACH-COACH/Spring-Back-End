package com.example.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class NewsResDto {
    private String newsTitle; // 뉴스 제목
    private String newsDate; // 뉴스 날짜
    private String newsDescription; // 뉴스 내용
    private String newsUrl; // 뉴스 원문 url
    private String newsKeywords; // 뉴스와 연관된 (생애주기 - 목표)
    private String newsImg; // 뉴스 썸네일 이미지
}
