package com.example.demo.config;

import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

public class ElasticConfig extends ElasticsearchConfiguration {
	
	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
				.connectedTo("localhost:9200")
				.build();

	}

}
