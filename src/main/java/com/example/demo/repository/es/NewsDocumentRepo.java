package com.example.demo.repository.es;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;
import com.example.demo.model.document.NewsDocument;

public interface NewsDocumentRepo extends ElasticsearchRepository<NewsDocument, String>{
    @Query("{\"bool\": {\"must\": [{\"match\": {\"news_keywords\": \"?0\"}}]}}")
    List<NewsDocument> findByExactKeywords(String keywords);
}
