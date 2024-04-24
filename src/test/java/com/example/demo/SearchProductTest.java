package com.example.demo;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.model.document.ProductDocument;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.repository.es.ProductDocumentRepo;
import com.example.demo.service.ProductService;

@SpringBootTest
public class SearchProductTest {
	
	@Autowired
	ProductDocumentRepo productDocRepo; 
	
	@Autowired
	ProductService productService;
	
	@Test
	void 엘라스틱서치조건검색() {
		String productType = "SAVINGS";
		int maturity = 12;
		String keyword = "여행";
		String depositCycle = "FIXED";
		
		
//		Iterable<ProductDocument> pList = productDocRepo.findAllByProductNameContainsAndProductType(keyword, productType);
		Iterable<ProductDocument> pList = productDocRepo.findAllByProductNameContainsAndProductTypeAndDepositCycleAndMaturity(keyword, productType,depositCycle,maturity);

		for (ProductDocument p:pList) {
			System.out.println(p.toString());
			System.out.println();
		}
	}
	
	@Test
	void searchProductsTest() {
		
		// given
		Optional<String> productType = Optional.of("SAVINGS");
		Integer maturity = 12;
		String keyword = "여행";
//		Optional<String> depositCycle = Optional.of("FLEXIBLE");
		Optional<String> depositCycle = Optional.ofNullable(null);
		
		SearchProductReqDto criteria = new SearchProductReqDto().builder()
				.keyword(keyword)
				.maturity(maturity)
				.depositCycle(depositCycle)
				.productType(productType)
				.build();
		
		// Pageable 객체 생성
        // PageRequest.of(pageNumber, pageSize, Sort)
        // 여기서는 정렬 없이 페이지 번호 0 (첫 페이지), 페이지 크기 10으로 설정
        Pageable pageable = PageRequest.of(0, 10);
		
        // when
        Page<ProductDocument> result = productService.searchProducts(criteria, pageable);
        
        // then
        result.getContent().forEach(System.out::println);
	}
	
	@Test
	void 상품상세() {
		try {
			ProductDocument dto = productService.getProductDetail("ADzj6Y4BBqj7mtS5krt9");
			System.out.println(dto.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
