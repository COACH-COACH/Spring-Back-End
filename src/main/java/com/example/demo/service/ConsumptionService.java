package com.example.demo.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class ConsumptionService {
	
	private final ElasticsearchClient client;

	@Autowired
	private ConsumptionService(ElasticsearchClient client){
		this.client = client;
	}
	
	// 전체 분기 집계 - 지난날 동안 가장 많은 소비를 보인 카테고리
    public Map<String, Object> aggregateTotalSpendingBySeq(String seq) throws IOException {
        if (seq == null || seq.isEmpty()) {
            throw new IllegalArgumentException("SEQ가 없습니다.");
        }
    	
        Map<String, Integer> totalSpending = new HashMap<>();

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index("consumption-original")
            .query(q -> q
                .bool(b -> b
                    .filter(f -> f
                        .term(t -> t
                            .field("SEQ")
                            .value(v -> v.stringValue(seq))
                        )
                    )
                )
            )
        );

        SearchResponse<Map> response = client.search(searchRequest, Map.class);
        
        for (Hit<Map> hit : response.hits().hits()) {
            Map<String, Object> fields = hit.source();
            fields.forEach((key, value) -> {
                if (value instanceof Integer) {
                    totalSpending.merge(key, (Integer) value, Integer::sum);
                }
            });
        }
        
        // 가장 높은 지출을 찾기
        String highestSpendingCategory = "";
        int highestAmount = 0;
        
        String[] categories = {
                "FUNITR_AM", "APPLNC_AM", "HLTHFS_AM", "BLDMNG_AM", "ARCHIT_AM", "OPTIC_AM", "AGRICTR_AM",
                "LEISURE_S_AM", "LEISURE_P_AM", "CULTURE_AM", "SANIT_AM", "INSU_AM", "OFFCOM_AM", "BOOK_AM",
                "RPR_AM", "HOTEL_AM", "GOODS_AM", "TRVL_AM", "FUEL_AM", "SVC_AM", "DISTBNP_AM", "DISTBP_AM",
                "GROCERY_AM", "HOS_AM", "CLOTH_AM", "RESTRNT_AM", "AUTOMNT_AM", "AUTOSL_AM", "KITWR_AM",
                "FABRIC_AM", "ACDM_AM", "MBRSHOP_AM"
            };
        
        for (String category : categories) {
            if (totalSpending.containsKey(category) && totalSpending.get(category) > highestAmount) {
                highestSpendingCategory = category;
                highestAmount = totalSpending.get(category);
            }
        }

        // 결과를 JSON 형식의 문자열로 출력
        Map<String, Object> result = new HashMap<>();
        result.put("category", highestSpendingCategory);
        result.put("amt", highestAmount);
        
        return result;
    }
    
    // 분기별 집계 -
//    public Map<String, Integer> ComparedByBasYh(String seq, String quarter) throws IOException {
//        Map<String, Integer> totalSpending = new HashMap<>();
//
//        SearchRequest searchRequest = SearchRequest.of(s -> s
//            .index("consumption-original")
//            .query(q -> q
//                .bool(b -> b
//                    .filter(f -> f
//                        .term(t -> t
//                            .field("SEQ")
//                            .value(v -> v.stringValue(seq))
//                            .field(quarter)
//                            .value(v -> v.stringValue(quarter))
//                        )
//                    )
//                )
//            )
//        );
//
//        SearchResponse<Map> response = client.search(searchRequest, Map.class);
//        
//        for (Hit<Map> hit : response.hits().hits()) {
//            Map<String, Object> fields = hit.source();
//            fields.forEach((key, value) -> {
//                if (value instanceof Integer) {
//                    totalSpending.merge(key, (Integer) value, Integer::sum);
//                }
//            });
//        }
//        return totalSpending;
//    }    
}

	