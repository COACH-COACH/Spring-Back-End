package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.exception.GoalLimitExceededException;
import com.example.demo.model.dto.GoalDto;
import com.example.demo.model.dto.request.CreateGoalReqDto;
import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.model.dto.response.GoalStatisticsResDto;
import com.example.demo.model.entity.Enroll;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.LifeStage;
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
	
	private final Map<LifeStage, List<String>> lifeStageGoals = new HashMap<>();
	
	@Autowired
	public GoalService(GoalRepo goalRepo, UserRepo userRepo, EnrollRepo enrollRepo, ProductRepo productRepo) {
		this.goalRepo = goalRepo;
		this.userRepo = userRepo;
		this.enrollRepo = enrollRepo;
		this.productRepo = productRepo;
		setLifeStageGoal();
	}
	
	public void setLifeStageGoal() {
        lifeStageGoals.put(LifeStage.UNI, List.of("학자금", "여행", "주택", "어학연수", "전자기기", "기타목돈"));
        lifeStageGoals.put(LifeStage.NEW_JOB, List.of("자가마련", "자차마련", "결혼자금", "반려동물", "기타목돈"));
        lifeStageGoals.put(LifeStage.NEW_WED, List.of("자가마련", "자차마련", "자녀준비", "투자비용", "기타목돈"));
        lifeStageGoals.put(LifeStage.NO_CHILD, List.of("반려동물", "자가마련", "은퇴자금", "투자비용", "기타목돈"));
        lifeStageGoals.put(LifeStage.HAVE_CHILD, List.of("자가마련", "자녀관련목돈", "은퇴자금", "투자비용", "기타목돈"));
        lifeStageGoals.put(LifeStage.RETIR, List.of("건강", "창업비용", "취미", "자기계발", "기타목돈"));
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

	    // 완료되지 않은 목표 조회
	    List<Goal> goals = goalRepo.findByUserIdAndGoalSt(user.getId(), (byte) 0);
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
	                .goalRate(accurateGoalRate(enroll.getAccumulatedBalance(), goal.getTargetCost()))
	                .accountNum(enroll.getAccountNum())
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
	
	private float accurateGoalRate(BigDecimal enrollCost, BigDecimal goalCost) {
		return enrollCost.floatValue() / goalCost.floatValue() * 100;
	}

	public List<GoalStatisticsResDto> getGoalStatList(String username) {
		// 1. user의 라이프스테이지에 맞는 목표 가져오기
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    
	    List<GoalStatisticsResDto> statisticsList = new ArrayList<>();
	    
	    // 2. statisticsList 초기화
        lifeStageGoals.getOrDefault(user.getLifeStage(), List.of()).forEach(goalName -> {
            statisticsList.add(GoalStatisticsResDto.builder()
                    .goalName(goalName)
                    .goalRate(0.0f)
                    .goalAvgTargetAmt(BigDecimal.ZERO)
                    .build());
        });
		
		// 3. 목표별 group by 해서 통계량 산출
	    List<Object[]> results = goalRepo.findGoalStatistics();
        for (Object[] result : results) {
            String goalName = (String) result[0];
            float goalRate = ((Number) result[1]).floatValue();
            BigDecimal goalAvgTargetAmt = BigDecimal.valueOf(((Number) result[2]).doubleValue());

            statisticsList.stream()
                    .filter(stat -> stat.getGoalName().equals(goalName))
                    .findFirst()
                    .ifPresent(stat -> {
                        stat.setGoalRate(goalRate);
                        stat.setGoalAvgTargetAmt(goalAvgTargetAmt);
                    });
        }

        return statisticsList;
	}
	
	public GoalDto addGoal(CreateGoalReqDto reqDto, String username) {
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    
	    // TODO: 1. 현재 유저의 목표가 3개인 경우 추가 불가
	    List<Goal> curGoalList = goalRepo.findByUserIdAndGoalSt(user.getId(), (byte) 0);
	    if (curGoalList.size() >= 3) {
	    	throw new GoalLimitExceededException("사용자는 최대 3개의 목표만 가질 수 있습니다.");
	    }
	    
	    // 2. 아닌 경우 목표 추가
		Goal newGoal = new Goal().builder()
				.user(user)
				.goalName(reqDto.getGoalName())
				.targetCost(reqDto.getTargetCost())
				.goalPeriod(reqDto.getGoalPeriod())
				.startDate(new Date())
				.accumulatedBalance(BigDecimal.ZERO)
				.goalSt((byte) 0)
				.build();
		Goal result = goalRepo.save(newGoal);
		return result.toDto();
	}

}
