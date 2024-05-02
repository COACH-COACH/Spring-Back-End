package com.example.demo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.document.ProductDocument;
import com.example.demo.model.document.SearchKeywordDocument;
import com.example.demo.model.dto.EnrollDto;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.repository.es.ProductDocumentRepo;
import com.example.demo.repository.es.SearchKeywordDocumentRepo;

import jakarta.transaction.Transactional;

import com.example.demo.repository.EnrollRepo;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.model.entity.Enroll;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.DepositCycle;
import com.example.demo.model.enums.ProductType;
import com.example.demo.model.dto.request.RecommendationProductReqDto;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.model.dto.response.ConnectGoalwithProductResDto;
import com.example.demo.model.dto.response.RecommendationResDto;
import com.example.demo.model.dto.response.RecommendationResDto.ItemRecommendationDto;;

@Service
public class ProductService {
	private final ElasticsearchOperations elasticsearchOperations;
	private final ProductDocumentRepo productDocumentRepo;
	private final SearchKeywordDocumentRepo searchKeywordDocumentRepo;
	private final UserRepo userRepo;
	private final ProductRepo productRepo;
	private final GoalRepo goalRepo;
	private final EnrollRepo enrollRepo;
	private final RestTemplate restTemplate;
	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
	public ProductService(ElasticsearchOperations elasticsearchOperations,
			ProductDocumentRepo productDocumentRepo, SearchKeywordDocumentRepo searchKeywordDocumentRepo,
			UserRepo userRepo, ProductRepo productRepo,GoalRepo goalRepo, EnrollRepo enrollRepo, RestTemplate restTemplate) {
		this.elasticsearchOperations = elasticsearchOperations;
		this.productDocumentRepo = productDocumentRepo;
		this.searchKeywordDocumentRepo = searchKeywordDocumentRepo;
		this.userRepo = userRepo;
		this.productRepo = productRepo;
		this.goalRepo = goalRepo;
		this.enrollRepo = enrollRepo;
		this.restTemplate = restTemplate;
	}

	// Test Service: Query Method 형태로 접근
	public List<ProductDocumentDto> getAllProduct() {
		Iterable<ProductDocument> docList = productDocumentRepo.findAll();
        return StreamSupport.stream(docList.spliterator(), false) // false는 병렬 처리를 하지 않겠다는 의미
            .map(ProductDocument::toDto)  // 각 ProductDocument를 ProductDocumentDto로 변환
            .collect(Collectors.toList()); // 결과를 List로 수집
	}
	
	// flask에서 추천된 상품 json파일 가져오기
	public RecommendationResDto getRecommendations(int userId){
		String url = "http://localhost:5000/recommendation/" + userId;
		ResponseEntity<HashMap> response = restTemplate.getForEntity(url, HashMap.class);  
		HashMap<String, List<HashMap>> rawData = response.getBody();
		
		return mapToProductRecommendationDto(rawData);
	}
    private RecommendationResDto mapToProductRecommendationDto(HashMap<String, List<HashMap>> rawData) {
        RecommendationResDto dto = RecommendationResDto.builder()
                .clusterRecommendations(getItemRecommendationList(rawData, "cluster_recommendations"))
                .itemRecommendations(getItemRecommendationList(rawData, "item_recommendations"))
                .staticRecommendations(getItemRecommendationList(rawData, "static_recommendations"))
                .build();
        return dto;
    }

    private List<ItemRecommendationDto> getItemRecommendationList(HashMap<String, List<HashMap>> rawData, String key) {
        if (rawData != null && rawData.containsKey(key)) {
            List<HashMap> maps = rawData.get(key);
            List<ItemRecommendationDto> items = new ArrayList<>();
            for (HashMap<String, Object> map : maps) {
                items.add(mapToItemRecommendationDto(map));
            }
            return items;
        }
        return new ArrayList<>();
    }

    private ItemRecommendationDto mapToItemRecommendationDto(HashMap<String, Object> rawData) {
        return ItemRecommendationDto.builder()
                .idPk((Integer) rawData.get("ID_PK"))
                .maturity((Integer) rawData.get("MATURITY"))
                .maxInterestRate(new BigDecimal(String.valueOf(rawData.get("MAX_INTEREST_RATE"))))
                .productName((String) rawData.get("PRODUCT_NAME"))
                .build();
}

    // 접속한 고객의 userId 가져오기
	public int getUserId(String loginId) {
		User user = userRepo.findByLoginId(loginId);
		if (user == null) {
			throw new UsernameNotFoundException("다음 로그인 아이디에 해당하는 유저가 없습니다: " + loginId);
		}
		return user.getId();
	}
	
	// 상품 검색
	public Page<ProductDocument> searchProducts(SearchProductReqDto dto, Pageable pageable) {
        Criteria criteria = new Criteria();
        if (dto.getProductType().isPresent()) {
        	criteria = criteria.and(new Criteria("PRODUCT_TYPE").is(dto.getProductType().get()));
        }
        
        if (dto.getDepositCycle().isPresent()) {
        	criteria.subCriteria(new Criteria("DEPOSIT_CYCLE").is(dto.getDepositCycle().get()));
        }
        
        if (dto.getMaturity() != null) {
        	criteria.subCriteria(new Criteria("MATURITY").is(dto.getMaturity()));
        }
        
        if (dto.getKeyword() != null) {
            Criteria productCriteria = new Criteria("PRODUCT_NAME").matches("*" + dto.getKeyword() + "*");
            Criteria conditionCriteria = new Criteria("PREFER_CONDITION").matches("*" + dto.getKeyword() + "*");
            Criteria detailCriteria = new Criteria("PRODUCT_DETAIL").matches("*" + dto.getKeyword() + "*");
            criteria.subCriteria(new Criteria().and(productCriteria).or(conditionCriteria).or(detailCriteria));
        }
        
        // 실제 검색 수행 쿼리
        CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
        // SearchHits = 검색 결과
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);

        // SearchHits 객체를 Page 객체로 변환
        List<ProductDocument> searchHitsContent = searchHits.getSearchHits().stream()
            .map(hit -> hit.getContent())
            .collect(Collectors.toList());
        
        return new PageImpl<>(searchHitsContent, pageable, searchHits.getTotalHits());
    }
	
	// 검색 키워드 가져오기
    public Map<String, List<Integer>> getKeywords(String seq) {
        if (seq == null || seq.isEmpty()) {
            throw new IllegalArgumentException("SEQ가 없습니다.");
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        String endDate = now.format(formatter); // "now"
        String startDate = now.minusDays(3).format(formatter); // "now-3d" 
   
    	Criteria criteria = new Criteria("seq").is(seq);
        
    	List<SearchKeywordDocument> results = searchKeywordDocumentRepo.findBySeqAndTimestampBetween(seq, startDate, endDate);    	
        Map<String, List<Integer>> keywordToProductIds = new HashMap<>();

        if (!results.isEmpty()) {
            results.forEach(document -> {
                String keyword = document.getKeyword();
                Set<Integer> productIds = new HashSet<>();
                
                List<ProductDocument> products = new ArrayList<>();
                products.addAll(productDocumentRepo.findByProductNameContaining(keyword));
                products.addAll(productDocumentRepo.findByProductDetailContaining(keyword));
                products.addAll(productDocumentRepo.findByPreferConditionContaining(keyword));
                
                products.forEach(product -> productIds.add(product.getIdPk()));
                
//                List<Integer> productIds = products.stream().map(ProductDocument::getIdPk).collect(Collectors.toList());
                keywordToProductIds.put(keyword, new ArrayList<>(productIds));
            });
        }
        return keywordToProductIds;
    }
	
	// 상품 상세설명 - es
	public ProductDocument getProductDetail(String productId) throws Exception {
		Optional<ProductDocument> res = productDocumentRepo.findById(productId);
		if (!res.isPresent()) {
			throw new Exception("상품 ID가 존재하지 않습니다.");
		}
		return res.get();
	}

	// 상품 상세설명 - sql
	public Product getProductDetailSql(int productId) throws Exception {
		Optional<Product> res = productRepo.findById(productId);
		if (!res.isPresent()) {
			throw new Exception("상품 ID가 존재하지 않습니다.");
		}
		return res.get();
	}
	
	
	// 계좌번호 생성 메서드
	public class AccountNumberGenerator {

	    private static final Random random = new Random();

	    public static String generateAccountNumber() {
	        // 예: 3자리-3자리-2자리-5자리 형태의 계좌 번호를 생성
	        return String.format("%03d-%03d-%02d-%05d",
	                random.nextInt(1000),  // 0에서 999 사이의 숫자
	                random.nextInt(1000),  // 0에서 999 사이의 숫자
	                random.nextInt(100),   // 0에서 99 사이의 숫자
	                random.nextInt(100000) // 0에서 99999 사이의 숫자
	        );
	    }
	}
	
	
	// 상품 가입 - 상품과 목표 연동을 위한 목표 list
	@Transactional
	public ResponseEntity<Object> connectGoalwithProduct(int userId) {
	    List<Goal> goals = goalRepo.findByUserIdAndGoalSt(userId, (byte) 0);
	    Map<String, Object> response = new HashMap<>();
	    
	    if (goals.isEmpty()) {
	        response.put("goals", Collections.emptyList());
	        response.put("message", "목표가 없습니다. 목표를 생성해 보세요!");
	        return ResponseEntity.ok(response);
	    }

	    List<ConnectGoalwithProductResDto.GoalListDto> goalListDto = new ArrayList<>();
	    boolean allEnrolled = true;
	    
	    for (Goal goal : goals) {
	        Optional<Enroll> enrollOptional = enrollRepo.findOptionalByUserIdAndGoalId(userId, goal.getId());
	        if (!enrollOptional.isPresent()) {
	            ConnectGoalwithProductResDto.GoalListDto dto = ConnectGoalwithProductResDto.GoalListDto.builder()
	                    .goalId(goal.getId())
	                    .goalName(goal.getGoalName())
	                    .startDate(goal.getStartDate())
	                    .build();
	            goalListDto.add(dto);
	            allEnrolled = false;
	        }
	    }

	    if (allEnrolled) {
	        response.put("goals", Collections.emptyList());
	        response.put("message", "이미 모든 목표에 상품이 가입되어 있습니다.");
	        return ResponseEntity.ok(response);
	    }

	    response.put("goals", goalListDto);
	    response.put("message", ""); // 목표가 있을 때는 빈 메시지
	    return ResponseEntity.ok(response);
	}


	
	// 상품 가입 - DB에 저장
	@Transactional
	public void registerProduct(int userId, int productId, int goalId, RecommendationProductReqDto requestDto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        Goal goal = goalRepo.findById(goalId)
        		.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표입니다."));
            
            Date startDate = new Date();											
            Calendar calendar = Calendar.getInstance();								
            calendar.setTime(startDate);											// 상품 가입 날짜로 START_DATE 설정
            calendar.add(Calendar.MONTH, product.getMaturity());					// START_DATE에서 상품 기간 만큼 더해서 END_DATE 설정
            System.out.println("StartDate: " + startDate);
            System.out.println("EndDate after adding maturity: " + calendar.getTime());

            String accountNumber = AccountNumberGenerator.generateAccountNumber();	// 계좌번호 생성
            BigDecimal maturity = new BigDecimal(product.getMaturity());
            
            Enroll enroll = Enroll.builder()
                    .user(user)
                    .product(product)
                    .goal(goal)
                    .startDate(startDate)
                    .endDate(calendar.getTime())
                    .maturitySt((byte) 0) 			// 처음 가입시 0으로 설정
                    .accountNum(accountNumber)
                    .build();
        // DEPOSIT
        if (product.getProductType()==ProductType.DEPOSIT) {
            BigDecimal depositAmount = requestDto.getDepositAmount();
            BigDecimal targetCostSavings = maturity.multiply(depositAmount); // 적금 상품 목표 금액 생성 
	          enroll.setAccumulatedBalance(requestDto.getDepositAmount());
	          enroll.setTargetCost(requestDto.getDepositAmount());			// 예치금을 목표 금액으로
	          enroll.setAccumulatedBalance(requestDto.getDepositAmount()); 	// 예치금을 계좌 잔액에 예치
        } 
        // SAVINGS & FIX
        else if (product.getDepositCycle()==DepositCycle.FIXED) {
            BigDecimal depositAmount = requestDto.getDepositAmount();
            BigDecimal targetCostSavings = maturity.multiply(depositAmount);	// 적금 상품 목표 금액 생성 
	          enroll.setDepositAmtCycle(requestDto.getDepositAmount());			// 매달 입금할 금액 -> 목표 금액 계산에 이용
	          enroll.setAccumulatedBalance(requestDto.getFirstDeposit());		// 초기 입금액을 계좌 잔액에 예치
	          enroll.setTargetCost(targetCostSavings);							// 목표 금액 계산 후 DB에 저장
        } 
        // SAVINGS & FLEXIBLE
        else {
        	enroll.setAccumulatedBalance(requestDto.getFirstDeposit());		// 초기 입금액을 계좌 잔액에 예치
        	enroll.setTargetCost(requestDto.getGoalAmount()); 				// 목표금액을 직접 입력받음
        }
        enrollRepo.save(enroll);
        log.info("Enroll saved: {}", enroll);
        }
}
