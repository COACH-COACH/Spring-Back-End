package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
import com.example.demo.model.dto.EnrollDto;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.repository.es.ProductDocumentRepo;

import jakarta.transaction.Transactional;

import com.example.demo.repository.EnrollRepo;
import com.example.demo.repository.GoalRepo;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.model.entity.Enroll;
import com.example.demo.model.entity.Goal;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.ProductType;
import com.example.demo.model.dto.request.RecommendationProductReqDto;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.model.dto.response.RecommendationResDto;
import com.example.demo.model.dto.response.RecommendationResDto.ItemRecommendationDto;;

@Service
public class ProductService {
	private final ElasticsearchOperations elasticsearchOperations;
	private final ProductDocumentRepo productDocumentRepo;
	private final UserRepo userRepo;
	private final RestTemplate restTemplate;
	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
	private final ProductRepo productRepo;
	private final GoalRepo goalRepo;
	private final EnrollRepo enrollRepo;
	
	@Autowired
	public ProductService(ElasticsearchOperations elasticsearchOperations, ProductDocumentRepo productDocumentRepo, UserRepo userRepo, 
			ProductRepo productRepo,GoalRepo goalRepo, EnrollRepo enrollRepo, RestTemplate restTemplate) {
		this.elasticsearchOperations = elasticsearchOperations;
		this.productDocumentRepo = productDocumentRepo;
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

	public ProductDocument getProductDetail(String productId) throws Exception {
		Optional<ProductDocument> res = productDocumentRepo.findById(productId);
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
	
	
	// 상품 가입 - 상품과 목표 연동
	
	
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
            calendar.setTime(startDate);
            calendar.add(Calendar.MONTH, product.getMaturity());
            String accountNumber = AccountNumberGenerator.generateAccountNumber();
            
            Enroll enroll = Enroll.builder()
                    .user(user)
                    .product(product)
                    .goal(goal)
                    .startDate(startDate)
                    .endDate(calendar.getTime())
                    .maturitySt((byte) 0) 			// 처음 가입시 0으로 설정
                    .accountNum(accountNumber)
                    .build();
        if (product.getProductType()==ProductType.SAVINGS) {
            enroll.setDepositAmountCycle(requestDto.getDepositAmount());
        } else if (product.getProductType()==ProductType.DEPOSIT) {
            enroll.setAccumulatedBalance(requestDto.getDepositAmount());
        }
        enrollRepo.save(enroll);
        log.info("Enroll saved: {}", enroll);
        }
	
	
}
