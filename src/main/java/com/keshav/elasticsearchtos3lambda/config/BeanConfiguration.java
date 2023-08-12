package com.keshav.elasticsearchtos3lambda.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class BeanConfiguration {

	private final Logger LOGGER = LogManager.getLogger(BeanConfiguration.class);

	@Value("${aws.elasticsearch.server.url:https://keshav-deployment.es.us-east-1.aws.found.io}")
    private String serverUrl; // AWS ElasticSearch Server Url
	@Value("${aws.elasticsearch.api.key:ODZqRjZZa0JsMEQ0MXFwQ3ZvblU6alNWUHNSYW5TbFNOOVJObThLQVZfdw==}")
    private String apiKey; // AWS ElasticSearch API key
    @Value("${aws.region:us-east-1}")
    private String region; // AWS region

	@Bean
	public AmazonS3 amazonS3Client() {
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).build();
	}

	@Bean
	public ElasticsearchClient elasticSearchClient() {
		LOGGER.info("elasticSearchClient: Server Url: {}, apiKey: {}", serverUrl, apiKey);
		// Create the low-level client
		RestClient restClient = RestClient
		    .builder(HttpHost.create(serverUrl))
		    .setDefaultHeaders(new Header[]{
		        new BasicHeader("Authorization", "ApiKey " + apiKey)
		    })
		    .build();

		// Create the transport with a Jackson Mapper
		ElasticsearchTransport transport = new RestClientTransport(
		    restClient, new JacksonJsonpMapper());

		// And create the API client
		return new ElasticsearchClient(transport);
	}
}