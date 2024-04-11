package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.model.dto.response.GoalListResDto.GoalAndProductDto;
import com.example.demo.model.entity.Enroll;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.repository.EnrollRepo;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;

@Service
public class GoalService {
	private GoalRepo goalRepo;
	private UserRepo userRepo;
	private EnrollRepo enrollRepo;
	private ProductRepo productRepo;
	
	@Autowired
	public GoalService(GoalRepo goalRepo, UserRepo userRepo, EnrollRepo enrollRepo, ProductRepo productRepo) {
		this.goalRepo = goalRepo;
		this.userRepo = userRepo;
		this.enrollRepo = enrollRepo;
		this.productRepo = productRepo;
	}
	
	public List<GoalDto> getGoalListByUsername(String username) {
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
	        new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    
		// 해당 사용자의 목표 목록 조회
	    List<Goal> goals = goalRepo.findByUserId(user.getId());
		return goals.stream().map(Goal::toDto).collect(Collectors.toList());
	}

	public GoalListResDto getGoalProductListByUsername(String username) {
	    Optional<User> userOptional = Optional.ofNullable(userRepo.findByLoginId(username));
	    if (!userOptional.isPresent()) {
	        throw new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username);
	    }
	    User user = userOptional.get();

	    List<Goal> goals = goalRepo.findByUserId(user.getId());
	    if (goals.isEmpty()) {
	        return new GoalListResDto(); // 목표가 없을 시 빈 객체 반환
	    }

	    List<GoalListResDto.GoalAndProductDto> goalProductList = new ArrayList<>();
	    for (Goal goal : goals) {
	        Optional<Enroll> enrollOptional = Optional.ofNullable(enrollRepo.findByUserIdAndGoalId(user.getId(), goal.getId()));
	        if (!enrollOptional.isPresent()) { // 해당 목표로 가입된 상품이 없을 시
	        	GoalListResDto.GoalAndProductDto goalProductDto = GoalListResDto.GoalAndProductDto.builder()
		                .goalId(goal.getId())
		                .goalName(goal.getGoalName())
		                .targetCost(goal.getTargetCost())
		                .goalSt(goal.getGoalSt())
		                .startDate(goal.getStartDate())
		                .goalPeriod(goal.getGoalPeriod())
		                .totalBalance(goal.getAccumulatedBalance())
		                .build();
		        goalProductList.add(goalProductDto);
	            continue; 
	        }
	        Enroll enroll = enrollOptional.get();
	        
	        Product product = null;
	        if (enroll.getProduct() != null) {
	            Optional<Product> productOptional = productRepo.findById(enroll.getProduct().getId());
	            if (productOptional.isPresent()) {
	                product = productOptional.get();
	            }
	        }

	        GoalListResDto.GoalAndProductDto goalProductDto = GoalListResDto.GoalAndProductDto.builder()
	                .goalId(goal.getId())
	                .goalName(goal.getGoalName())
	                .targetCost(goal.getTargetCost())
	                .goalSt(goal.getGoalSt())
	                .startDate(goal.getStartDate())
	                .goalPeriod(goal.getGoalPeriod())
	                .totalBalance(goal.getAccumulatedBalance())
	                .enrollId(enroll.getId())
	                .accumulatedBalance(enroll.getAccumulatedBalance())
	                .productId(product != null ? product.getId() : null) // product가 null이면 productId는 null
	                .productName(product != null ? product.getProductName() : null) // product가 null이면 productName는 null
	                .build();
	        goalProductList.add(goalProductDto);
	    }

	    return GoalListResDto.builder()
	            .userId(user.getId())
	            .fullName(user.getFullName())
	            .goals(goalProductList)
	            .build();
	}

}
