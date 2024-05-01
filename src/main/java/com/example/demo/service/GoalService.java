package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.example.demo.model.dto.response.GoalCategoryResDto;
import com.example.demo.model.dto.response.GoalCategoryResDto.GoalName;
import com.example.demo.model.dto.response.GoalListResDto;
import com.example.demo.model.dto.response.GoalListResDto.GoalStatistics;
import com.example.demo.model.entity.Enroll;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.Plan;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.DepositCycle;
import com.example.demo.model.enums.LifeStage;
import com.example.demo.repository.EnrollRepo;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.PlanRepo;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GoalService {
	private GoalRepo goalRepo;
	private UserRepo userRepo;
	private EnrollRepo enrollRepo;
	private ProductRepo productRepo;
	private PlanRepo planRepo;
	
	private final Map<LifeStage, List<String>> lifeStageGoals = new HashMap<>();
	
	@Autowired
	public GoalService(GoalRepo goalRepo, UserRepo userRepo, EnrollRepo enrollRepo, ProductRepo productRepo, PlanRepo planRepo) {
		this.goalRepo = goalRepo;
		this.userRepo = userRepo;
		this.enrollRepo = enrollRepo;
		this.productRepo = productRepo;
		this.planRepo = planRepo;
		setLifeStageGoal();
	}
	
	public List<GoalDto> getGoalListByUsername(String username) {
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
	        new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    
		// 해당 사용자의 목표 목록 조회
	    List<Goal> goals = goalRepo.findByUserId(user.getId());
		return goals.stream().map(Goal::toDto).collect(Collectors.toList());
	}
	
	public void setLifeStageGoal() {
        lifeStageGoals.put(LifeStage.UNI, List.of("학자금", "여행", "주택", "어학연수", "전자기기", "기타목돈"));
        lifeStageGoals.put(LifeStage.NEW_JOB, List.of("자가마련", "자차마련", "결혼자금", "반려동물", "기타목돈"));
        lifeStageGoals.put(LifeStage.NEW_WED, List.of("자가마련", "자차마련", "자녀준비", "투자비용", "기타목돈"));
        lifeStageGoals.put(LifeStage.NO_CHILD, List.of("반려동물", "자가마련", "은퇴자금", "투자비용", "기타목돈"));
        lifeStageGoals.put(LifeStage.HAVE_CHILD, List.of("자가마련", "자녀관련목돈", "은퇴자금", "투자비용", "기타목돈"));
        lifeStageGoals.put(LifeStage.RETIR, List.of("건강", "창업비용", "취미", "자기계발", "기타목돈"));
    }
	
	// [메인 페이지] 목표 별 상품 조회 API
	public GoalListResDto getGoalProductListByUsername(String username) {
		User user = Optional.of(userRepo.findByLoginId(username))
	            .orElseThrow(() -> new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));

		// 1. 목표 목록 조회
        List<Goal> goals = goalRepo.findByUserIdAndGoalSt(user.getId(), (byte) 0);
        if (goals.isEmpty()) {
            return new GoalListResDto();
        }
        
        // 2. 각 목표 통계량 조회
        List<Object[]> statistics = goalRepo.findGoalPercentageByLifeStageAndGoalName(user.getLifeStage().toString()); // [[UNI, 여행, 16.66667], ...]
//        Map<String, Float> lifestageProportion = statistics.stream()
//        	    .collect(Collectors.toMap(
//        	        stat -> (String) stat[1], // GOAL_NAME을 키로 사용
//        	        stat -> ((Number) stat[2]).floatValue() // 비율을 Float로 변환
//        	    ));
        Map<String, GoalStatistics> goalStatisticsMap = new HashMap<>();
        
        for (Object[] record : statistics) {
            String goalName = (String) record[1];
            float proportion = ((Number) record[2]).floatValue();
            BigDecimal accumulatedBalance = new BigDecimal(record[3].toString());

            goalStatisticsMap.put(goalName, new GoalStatistics(proportion, accumulatedBalance));
        }

        // 3. 목표와 연결된 상품 리스트
        List<GoalListResDto.GoalAndProductDto> goalProductList = convertGoalsToDto(goals, user, goalStatisticsMap);
        
        // 4. Response DTO 생성
        return GoalListResDto.builder()
            .userId(user.getId())
            .fullName(user.getFullName())
            .goals(goalProductList)
            .build();
    }
	
	private List<GoalListResDto.GoalAndProductDto> convertGoalsToDto(List<Goal> goals, User user, Map<String, GoalStatistics> goalStatisticsMap) {
        List<GoalListResDto.GoalAndProductDto> goalProductList = new ArrayList<>();
        goals.forEach(goal -> {
            Optional<Enroll> res = enrollRepo.findByUserIdAndGoalId(user.getId(), goal.getId());
            if(res.isEmpty()) {
            	goalProductList.add(buildGoalProductDtoWithoutEnroll(goal, goalStatisticsMap));
                return;
            }
            Enroll enroll = res.get();
            Product product = enroll.getProduct();										// 2. 목표에 등록된 상품 O
            if (product.getDepositCycle().equals(DepositCycle.FLEXIBLE)) { 				// 2-1. 자유적금
                Plan plan = planRepo.findByEnroll_id(enroll.getId()).orElse(null);
                goalProductList.add(buildGoalProductDtoWithPlan(goal, enroll, product, plan, goalStatisticsMap));
            } else {																	// 2-2. 예금 or 정기적금
                goalProductList.add(buildGoalProductDtoWithProduct(goal, enroll, product, goalStatisticsMap));
            }
        });
        return goalProductList;
    }
	
	// 목표에 추가된 상품 X
	private GoalListResDto.GoalAndProductDto buildGoalProductDtoWithoutEnroll(Goal goal, Map<String, GoalStatistics> goalStatisticsMap) {
		GoalStatistics stats = goalStatisticsMap.getOrDefault(goal.getGoalName(), new GoalListResDto.GoalStatistics(0.0f, BigDecimal.ZERO));
        return GoalListResDto.GoalAndProductDto.builder()
            .goalId(goal.getId())
            .goalName(goal.getGoalName())
            .goalSt(goal.getGoalSt())
            .goalStartDate(goal.getStartDate())
            .goalStat(stats)
            .build();
    }
	
	// 목표에 추가된 상품 O(자유적금 O)
	private GoalListResDto.GoalAndProductDto buildGoalProductDtoWithPlan(Goal goal, Enroll enroll, Product product, Plan plan, Map<String, GoalStatistics> goalStatisticsMap) {
		GoalStatistics stats = goalStatisticsMap.getOrDefault(goal.getGoalName(), new GoalListResDto.GoalStatistics(0.0f, BigDecimal.ZERO));
        return GoalListResDto.GoalAndProductDto.builder()
                .goalId(goal.getId())
                .goalName(goal.getGoalName())
                .goalSt(goal.getGoalSt())
                .goalStartDate(goal.getStartDate())
                .enrollId(enroll.getId())
                .productStartDate(enroll.getStartDate())
                .accumulatedBalance(enroll.getAccumulatedBalance())
                .goalRate(accurateGoalRate(enroll.getStartDate(), enroll.getEndDate()))
                .accountNum(enroll.getAccountNum())
                .targetCost(enroll.getTargetCost())
                .productId(product.getId())
                .productName(product.getProductName())
                .depositCycle(setDepositCycle(product.getDepositCycle()))
                
                .actionPlan(plan.getActionPlan())
                .depositAmt(plan.getDepositAmt())
                .depositStartDate(plan.getDepositStartDate())
                .depositAmtCycle(plan.getDepositAmtCycle())
                .totalCount(plan.getTotalCount())
                .lastDepositDate(plan.getLastDepositDate())
                .goalStat(stats)
                .build();
    }

	// 목표에 추가된 상품 O(자유적금X)
    private GoalListResDto.GoalAndProductDto buildGoalProductDtoWithProduct(Goal goal, Enroll enroll, Product product, Map<String, GoalStatistics> goalStatisticsMap) {
		GoalStatistics stats = goalStatisticsMap.getOrDefault(goal.getGoalName(), new GoalListResDto.GoalStatistics(0.0f, BigDecimal.ZERO));
        return GoalListResDto.GoalAndProductDto.builder()
            .goalId(goal.getId())
            .goalName(goal.getGoalName())
            .goalSt(goal.getGoalSt())
            .goalStartDate(goal.getStartDate())
            .enrollId(enroll.getId())
            .productStartDate(enroll.getStartDate())
            .targetCost(enroll.getTargetCost())
            .accumulatedBalance(enroll.getAccumulatedBalance())
            .goalRate(accurateGoalRate(enroll.getStartDate(), enroll.getEndDate()))
            .accountNum(enroll.getAccountNum())
            .productId(product.getId())
            .productName(product.getProductName())
            .depositCycle(setDepositCycle(product.getDepositCycle()))
            .goalStat(stats)
            .build();
    }
    
    private String setDepositCycle(DepositCycle depositCycle) {
    	if (depositCycle.equals(DepositCycle.FIXED)) return "예금";
    	else if (depositCycle.equals(DepositCycle.FLEXIBLE)) return "자유적금";
    	else return "정기적금";
    }
	
    private float accurateGoalRate(Date startDate, Date endDate) {
        Date currentDate = new Date();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        long startMillis = startCal.getTimeInMillis();
        long currentMillis = currentCal.getTimeInMillis();
        long endMillis = endCal.getTimeInMillis();

        long totalDuration = endMillis - startMillis;
        long progressDuration = currentMillis - startMillis;

        float rate;
        if (totalDuration == 0) { // 시작일과 종료일이 같은 경우
            rate = 100.0f; // 100% 진행된 것으로 간주
        } else {
            rate = (float) progressDuration / totalDuration * 100.0f;
        }
        
        rate = Math.round(rate * 10) / 10.0f;
        return rate > 100.0f ? 100.0f : rate;
    }

	public GoalCategoryResDto getGoalStatList(String username) {
		// 1. user의 라이프스테이지에 맞는 목표 가져오기
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    
	    List<GoalName> statisticsList = new ArrayList<>();
	    
	    // 2. statisticsList 초기화
        lifeStageGoals.getOrDefault(user.getLifeStage(), List.of()).forEach(goalName -> {
            statisticsList.add(GoalName.builder()
                    .goalName(goalName)
                    .build());
        });
        
        // 3. user 정보와 결합
        GoalCategoryResDto resDto = GoalCategoryResDto.builder()
        		.fullName(user.getFullName())
        		.categoryList(statisticsList)
        		.build();

        return resDto;
	}
	
	public GoalDto addGoal(CreateGoalReqDto reqDto, String username) {
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    
	    // 1. 현재 유저의 목표가 3개인 경우 추가 불가
	    List<Goal> curGoalList = goalRepo.findByUserIdAndGoalSt(user.getId(), (byte) 0);
	    if (curGoalList.size() >= 3) {
	    	throw new GoalLimitExceededException("사용자는 최대 3개의 목표만 가질 수 있습니다.");
	    }
	    
	    // 2. 아닌 경우 목표 추가
		Goal newGoal = new Goal().builder()
				.user(user)
				.goalName(reqDto.getGoalName())
				.startDate(new Date())
				.goalSt((byte) 0)
				.build();
		Goal result = goalRepo.save(newGoal);
		return result.toDto();
	}

	public GoalDto updateGoalState(String username, int goalId) throws Exception {
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
        	new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    Goal goal = Optional.of(goalRepo.findByUserIdAndId(user.getId(), goalId)).orElseThrow(() -> 
	    	new EntityNotFoundException("해당 목표가 존재하지 않습니다."));
	    if (goal.getGoalSt() == (byte) 1) {
	    	throw new Exception("이미 완료된 목표입니다.");
	    }
	    
	    goal.setGoalSt((byte) 1);
	    goal.setEndDate(new Date());
	    return goalRepo.save(goal).toDto();
	}

	public void deleteGoal(String username, int goalId) throws Exception {
	    User user = Optional.of(userRepo.findByLoginId(username)).orElseThrow(() -> 
    		new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + username));
	    Goal goal = Optional.of(goalRepo.findByUserIdAndId(user.getId(), goalId)).orElseThrow(() -> 
    		new EntityNotFoundException("해당 목표가 존재하지 않습니다."));
	    
		// 목표에 상품이 추가되어 있는 경우 
	    if (goal.getEnroll() != null) {
	    	throw new Exception("상품이 연결되어 있어 제거할 수 없습니다.");
	    }
	    goalRepo.deleteById(goalId);
	}

}
