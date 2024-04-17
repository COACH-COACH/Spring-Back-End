package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
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
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.repository.es.ProductDocumentRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.model.entity.User;

import com.example.demo.model.dto.request.SearchProductReqDto;

@Service
public class ProductService {
	private final ElasticsearchOperations elasticsearchOperations;
	private final ProductDocumentRepo productDocumentRepo;
	private final UserRepo userRepo;
	private final RestTemplate restTemplate;
	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
	public ProductService(ElasticsearchOperations elasticsearchOperations, ProductDocumentRepo productDocumentRepo, UserRepo userRepo, RestTemplate restTemplate) {
		this.elasticsearchOperations = elasticsearchOperations;
		this.productDocumentRepo = productDocumentRepo;
		this.userRepo = userRepo;
		this.restTemplate = restTemplate;
	}

	// Test Service: Query Method 형태로 접근
	public List<ProductDocumentDto> getAllProduct() {
		Iterable<ProductDocument> docList = productDocumentRepo.findAll();
        return StreamSupport.stream(docList.spliterator(), false) // false는 병렬 처리를 하지 않겠다는 의미
            .map(ProductDocument::toDto)  // 각 ProductDocument를 ProductDocumentDto로 변환
            .collect(Collectors.toList()); // 결과를 List로 수집
	}
	
	public HashMap getRecommendations(int userId){
		String url = "http://localhost:5000/recommendation/" + userId;
		ResponseEntity<HashMap> response = restTemplate.getForEntity(url, HashMap.class);  
		return response.getBody();
	}

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
}
