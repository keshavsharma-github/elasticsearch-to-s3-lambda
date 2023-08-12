package com.keshav.elasticsearchtos3lambda.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.keshav.elasticsearchtos3lambda.service.beans.Movie;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class ElasticSearchService {
	private final Logger LOGGER = LogManager.getLogger(ElasticSearchService.class);

	@Value("${aws.elasticsearch.index.name:movies}")
	private String indexName;

	@Autowired
	private ElasticsearchClient elasticSearchClient;

	@Autowired
	private QueryBuilderService queryBuilderService;

	public SearchResponse<Movie> fetchDataFromElasticSearch() throws InterruptedException, IOException {
		LOGGER.info("fetchDataFromElasticSearch");
		Map<String, String> queryConfig = new HashMap<>();
		queryConfig.put("director", "Ron Howard");
		queryConfig.put("title", "Rush");
		queryConfig.put("year", "2000");
		List<Query> queries = queryBuilderService.buildFilterQueries(queryConfig);
		SearchResponse<Movie> response = elasticSearchClient
				.search(s -> s.index(indexName).query(q -> q.bool(b -> b.must(queries))), Movie.class);
		LOGGER.info("response: {}", response);
		return response;
	}
}