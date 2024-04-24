package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.model.dto.response.RecommendationResDto;
import com.example.demo.model.entity.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.request.RecommendationProductReqDto;
import com.example.demo.model.dto.request.SearchProductReqDto;
import com.example.demo.model.dto.response.ConnectGoalwithProductResDto;
import com.example.demo.model.dto.response.PagenationResDto;
import com.example.demo.util.DefaultResponse;
import com.example.demo.util.ResponseMessage;
import com.example.demo.util.StatusCode;

import lombok.extern.slf4j.Slf4j;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;
	
	@Autowired
	private UserService userService;

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
	public RecommendationResDto getRecommendations() {
		String username = SecurityUtil.getUsername();
		int userId = productService.getUserId(username);

		try {
			return productService.getRecommendations(userId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting recommendations", e);
		}
	}
	
	// 상품 상세 설명을 위한 데이터 처리 - string
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
			@RequestBody SearchProductReqDto criteria, @PageableDefault(page = 0, size = 10) Pageable pageable) {

		String username = SecurityUtil.getUsername();
		int userId = productService.getUserId(username);
		String seq = userService.getUser(userId).getSeq();

		log.info("Product Search API: {}, {}", kv("seq", seq), kv("keyword", criteria.getKeyword()));

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
			data.put("pagenation",
					PagenationResDto.builder().totalElements(result.getTotalElements())
							.totalPages(result.getTotalPages()).currentPage(result.getNumber())
							.pageSize(result.getSize()).build());

			return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_PRODUCT_SUCCESS, data));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(DefaultResponse.res(StatusCode.BAD_REQUEST, e.getMessage()));
		}
	}
	
	// 상품 상세 설명 조회
	@GetMapping("/detail/{productId}")
	public ResponseEntity<DefaultResponse<?>> searchProductDetail(@PathVariable String productId) {
		try {
			ProductDto productDto;
			if (productId.matches("\\d+")) {	// 정규표현식으로 숫자만 있는지 확인
				int id = Integer.parseInt(productId);
				Product sqlResult = productService.getProductDetailSql(id);
				productDto = sqlResult.toDto();
				return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK,ResponseMessage.READ_PRODUCT_SUCCESS, productDto));
			} else {
				ProductDocument esResult = productService.getProductDetail(productId);
				return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_PRODUCT_SUCCESS, transformProductData(esResult)));
			}
		} catch (NumberFormatException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(DefaultResponse.res(StatusCode.BAD_REQUEST, "Invalid product ID format."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(DefaultResponse.res(StatusCode.BAD_REQUEST, e.getMessage()));
		}
	}
		
	// 프론트에 목표 리스트 전달
	@GetMapping("/connect")
	public ResponseEntity<ConnectGoalwithProductResDto> connectGoalwithProduct(){
		String username = SecurityUtil.getUsername();
		int userId = productService.getUserId(username);
		ConnectGoalwithProductResDto responseDto = productService.connectGoalwithProduct(userId);
		return ResponseEntity.ok(responseDto);
	}
	
	// 상품 가입
	@PostMapping("/register/{productId}/{goalId}")
	public ResponseEntity<Void> registerProduct(@PathVariable int productId, @PathVariable int goalId,
			@RequestBody RecommendationProductReqDto requestDto) {
		String username = SecurityUtil.getUsername();
		int userId = productService.getUserId(username);
		
		// 가입된 상품 DB에 저장하기
		productService.registerProduct(userId, productId, goalId, requestDto);
		return ResponseEntity.ok().build();
	}

}
