package com.example.demo.repository.es;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.model.document.ProductDocument;

public interface ProductDocumentRepo extends ElasticsearchRepository<ProductDocument, String>{

	Iterable<ProductDocument> findAllByProductNameContainsAndProductType(String keyword, String productType);
	Iterable<ProductDocument> findAllByProductNameContainsAndProductTypeAndDepositCycleAndMaturity(String keyword, String productType, String depositCycle, int maturity);
	
    List<ProductDocument> findByProductNameContaining(String productName);
    List<ProductDocument> findByPreferConditionContaining(String condition);
    List<ProductDocument> findByProductDetailContaining(String detail);
}
