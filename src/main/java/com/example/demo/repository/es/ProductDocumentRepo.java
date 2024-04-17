package com.example.demo.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.model.document.ProductDocument;

public interface ProductDocumentRepo extends ElasticsearchRepository<ProductDocument, String>{

	Iterable<ProductDocument> findAllByProductNameContainsAndProductType(String keyword, String productType);

	Iterable<ProductDocument> findAllByProductNameContainsAndProductTypeAndDepositCycleAndMaturity(String keyword, String productType, String depositCycle, int maturity);
}
