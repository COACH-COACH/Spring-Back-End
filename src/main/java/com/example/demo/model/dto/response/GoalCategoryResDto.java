package com.example.demo.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GoalCategoryResDto {
	public String fullName;
	public List<GoalName> categoryList;

	@ToString
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter @Setter
	public static class GoalName {
		String goalName;
	}
}
