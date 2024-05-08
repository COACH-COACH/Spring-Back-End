package com.example.demo.model.document;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDocument {

    @Id
    private String id;

    @Field(name = "news_title", type = FieldType.Text)
    private String newsTitle;

    @Field(name = "news_date", type = FieldType.Date)
    private String newsDate;

    @Field(name = "news_description", type = FieldType.Text)
    private String newsDescription;

    @Field(name = "news_url", type = FieldType.Keyword)
    private String newsUrl;

    @Field(name = "news_keywords",type = FieldType.Text)
    private String newsKeywords;
    
    @Field(name = "news_img", type = FieldType.Keyword)
    private String newsImg;
}
