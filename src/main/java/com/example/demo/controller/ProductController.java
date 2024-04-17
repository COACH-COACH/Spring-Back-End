package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.model.dto.response.ProductRecommendationDto;
import com.example.demo.service.ProductService;
import com.example.demo.util.SecurityUtil;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@GetMapping("/") 
	public List<ProductDocumentDto> getAllProduct() {
		return productService.getAllProduct();
	}
	
	@GetMapping("/recommend")
	public HashMap getRecommendations(){
		String username = SecurityUtil.getUsername();
		int userId = productService.getUserId(username);
		
		try {
			return productService.getRecommendations(userId);
//			List<ProductRecommendationDto> recommendations = productService.getRecommendations(userId);
//			return ResponseEntity.ok(recommendations);
		} catch (Exception  e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting recommendations", e);
//			return ResponseEntity.internalServerError().build();
		}
	}

}

