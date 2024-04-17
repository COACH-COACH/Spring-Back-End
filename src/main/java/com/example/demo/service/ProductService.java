package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

import com.example.demo.model.document.ProductDocument;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.repository.es.ProductDocumentRepo;

@Service
public class ProductService {
	private final ProductDocumentRepo productDocumentRepo;
	private final ElasticsearchOperations elasticsearchOperations;
	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
	
	public ProductService(ElasticsearchOperations elasticsearchOperations, ProductDocumentRepo productDocumentRepo) {
		this.elasticsearchOperations = elasticsearchOperations;
		this.productDocumentRepo = productDocumentRepo;
	}

	// Test Service: Query Method 형태로 접근
	public List<ProductDocumentDto> getAllProduct() {
		Iterable<ProductDocument> docList = productDocumentRepo.findAll();
        return StreamSupport.stream(docList.spliterator(), false) // false는 병렬 처리를 하지 않겠다는 의미
            .map(ProductDocument::toDto)  // 각 ProductDocument를 ProductDocumentDto로 변환
            .collect(Collectors.toList()); // 결과를 List로 수집
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
