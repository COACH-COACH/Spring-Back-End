package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.document.ProductDocument;
import com.example.demo.model.dto.ProductDocumentDto;
import com.example.demo.repository.ProductDocumentRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.model.dto.response.ProductRecommendationDto;
import com.example.demo.model.entity.User;


@Service
public class ProductService {
	private final ProductDocumentRepo productDocumentRepo;
	private final UserRepo userRepo;
	private final RestTemplate restTemplate;
	
	@Autowired
	public ProductService(ProductDocumentRepo productDocumentRepo, UserRepo userRepo, RestTemplate restTemplate) {
		this.productDocumentRepo = productDocumentRepo;
		this.userRepo = userRepo;
		this.restTemplate = restTemplate;
	}

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

}
