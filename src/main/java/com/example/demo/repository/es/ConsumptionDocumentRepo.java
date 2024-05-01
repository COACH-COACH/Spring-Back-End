package com.example.demo.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.model.document.ConsumptionDocument;

public interface ConsumptionDocumentRepo extends ElasticsearchRepository<ConsumptionDocument, String> {

}