package com.example.demo.repository.es;


import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.model.document.SearchKeywordDocument;

public interface SearchKeywordDocumentRepo extends ElasticsearchRepository<SearchKeywordDocument, String> {
	List<SearchKeywordDocument> findBySeq(String seq);
    @Query("""
    {
      "bool": {
        "must": [
          {"match": {"seq": "?0"}}
        ],
        "filter": [
          {
            "range": {
              "@timestamp": {
                "gte": "?1",
                "lte": "?2"
              }
            }
          }
        ]
      }
    }
    """)
    List<SearchKeywordDocument> findBySeqAndTimestampBetween(String seq, String startDate, String endDate);

}