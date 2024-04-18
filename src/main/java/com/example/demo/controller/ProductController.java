package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.document.ProductDocument;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.model.dto.response.ProductRecommendationDto;
import com.example.demo.service.ProductService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.model.dto.response.PagenationResDto;
import com.example.demo.service.ProductService;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.StatusCode;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	// Test API
	@GetMapping("/") 
	public List<ProductDocumentDto> getAllProduct() {
		return productService.getAllProduct();
	}
	
	// 상품 추천
	@GetMapping("/recommend")
	public ProductRecommendationDto getRecommendations(){
		String username = SecurityUtil.getUsername();
		int userId = productService.getUserId(username);
		
		try {
			return productService.getRecommendations(userId);
		} catch (Exception  e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting recommendations", e);
		}
	}

	public ProductDocument transformProductData(ProductDocument product) {
        if ("DEPOSIT".equals(product.getProductType())) {
            product.setProductType("예금");
        } else if ("SAVINGS".equals(product.getProductType())) {
            product.setProductType("적금");
        }

        if ("FLEXIBLE".equals(product.getDepositCycle())) {
            product.setDepositCycle("자유적립식");
        } else if ("FIXED".equals(product.getDepositCycle())) {
            product.setDepositCycle("정액적립식");
        } else if ("HOLD".equals(product.getDepositCycle())) {
            product.setDepositCycle("거치식");
        }
		return product;
	}
	
	// 상품 검색
	@PostMapping("/search")
	public ResponseEntity<DefaultResponse<Map<String, Object>>> searchProductList(
	    @RequestBody SearchProductReqDto criteria, 
	    @PageableDefault(page = 0, size = 10) Pageable pageable) {

	    try {
	        Page<ProductDocument> result = productService.searchProducts(criteria, pageable);
	        List<ProductDocument> products = new ArrayList<>();
	        for (ProductDocument product : result.getContent()) {
	        	products.add(transformProductData(product));
	        }
	        
	        // 클라이언트에 전달할 데이터 설정
	        Map<String, Object> data = new HashMap<>();
	        data.put("products", products);
	        
	        new PagenationResDto();
			data.put("pagenation", PagenationResDto.builder()
					.totalElements(result.getTotalElements())
	        		.totalPages(result.getTotalPages())
	        		.currentPage(result.getNumber())
	        		.pageSize(result.getSize())
	        		.build());

	        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_PRODUCT_SUCCESS, data));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(DefaultResponse.res(StatusCode.BAD_REQUEST, e.getMessage()));
	    }
	}
	
	@GetMapping("/detail/{productId}")
	public ResponseEntity<DefaultResponse<ProductDocument>> searchProductDetail(@PathVariable String productId) {
		try {
			ProductDocument result = productService.getProductDetail(productId);
			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_PRODUCT_SUCCESS, transformProductData(result)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(DefaultResponse.res(StatusCode.BAD_REQUEST, e.getMessage()));
		}
	}

}

