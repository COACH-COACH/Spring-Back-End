package com.example.demo.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.model.document.ProductDocument;

public interface ProductDocumentRepo extends ElasticsearchRepository<ProductDocument, String>{

}
