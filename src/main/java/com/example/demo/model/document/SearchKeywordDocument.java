package com.example.demo.model.document;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "product-search-*")
public class SearchKeywordDocument {
    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String keyword;
    @Field(type = FieldType.Keyword)
    private String seq;
    @Field(type = FieldType.Date)
    private Date timestamp;
    
    public SearchKeywordDocument toDto() {
        return SearchKeywordDocument.builder()
            .id(this.getId())
            .keyword(this.getKeyword())
            .seq(this.getSeq())
            .timestamp(this.getTimestamp())
            .build();
    }
}
