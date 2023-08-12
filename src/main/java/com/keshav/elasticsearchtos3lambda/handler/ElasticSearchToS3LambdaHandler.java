package com.keshav.elasticsearchtos3lambda.handler;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonFactory;
import com.keshav.elasticsearchtos3lambda.service.ElasticSearchService;
import com.keshav.elasticsearchtos3lambda.service.S3Service;
import com.keshav.elasticsearchtos3lambda.service.beans.Movie;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpGenerator;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;

public class ElasticSearchToS3LambdaHandler implements RequestHandler<Object, String> {

	private final Logger LOGGER = LogManager.getLogger(ElasticSearchToS3LambdaHandler.class);
	@Autowired
	private S3Service s3Service;

	@Autowired
	private ElasticSearchService elasticSearchService;

	@SuppressWarnings("unused")
	public String handleRequest(Object input, Context context) {
		LOGGER.info("handleRequest input: {}", input);
		try {
			AnnotationConfigApplicationContext annotationAppContext = getAnnotationAppContext();
			SearchResponse<Movie> searchResponse = elasticSearchService.fetchDataFromElasticSearch();
			s3Service.putObjectTOS3(convertSearchResponseToJson(searchResponse));
			return "Data successfully pulled from ElasticSearch and pushed to S3.";
		} catch (Exception e) {
			LOGGER.error("handleRequest", e);
			return String.format("An error occurred: {}", e.getMessage());
		}
	}

	 private String convertSearchResponseToJson(SearchResponse<Movie> searchResponse) throws IOException {
		 final StringWriter writer = new StringWriter();
		 try (final JacksonJsonpGenerator generator = new JacksonJsonpGenerator(new JsonFactory().createGenerator(writer))) {
			 searchResponse.serialize(generator, new JacksonJsonpMapper());
		 }
		 return writer.toString();
	 }

	protected AnnotationConfigApplicationContext getAnnotationAppContext() {
		LOGGER.info("getAnnotationAppContext");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				"com.keshav.elasticsearchtos3lambda");
		ctx.getAutowireCapableBeanFactory().autowireBean(this);
		return ctx;
	}
}