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
public class AdvisorResDto {
	   private String customNews;
	    private String goalCheer;
	    private String consumptionCheer;
		public void setAchievementRate(double calculateAchievementRate) {
			
		}
}