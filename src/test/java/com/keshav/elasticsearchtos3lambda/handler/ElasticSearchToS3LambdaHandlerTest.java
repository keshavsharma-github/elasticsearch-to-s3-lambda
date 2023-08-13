package com.keshav.elasticsearchtos3lambda.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.keshav.elasticsearchtos3lambda.service.ElasticSearchService;
import com.keshav.elasticsearchtos3lambda.service.S3Service;
import com.keshav.elasticsearchtos3lambda.service.beans.Movie;

import co.elastic.clients.elasticsearch.core.SearchResponse;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchToS3LambdaHandlerTest {
	@InjectMocks
	@Spy
    private ElasticSearchToS3LambdaHandler underTest;

    @Mock
    private S3Service s3Service;

    @Mock
    private ElasticSearchService elasticSearchService;
    
    @Mock
    private AnnotationConfigApplicationContext mockAnnConfigAppContext;

    @SuppressWarnings("unchecked")
	@Test
    public void testHandleRequest_Success() throws Exception {
        SearchResponse<Movie> mockSearchResponse = mock(SearchResponse.class);
        doReturn(mockAnnConfigAppContext).when(underTest).getAnnotationAppContext();
        when(elasticSearchService.fetchDataFromElasticSearch()).thenReturn(mockSearchResponse);
        doNothing().when(s3Service).putObjectTOS3(anyString());

        String result = underTest.handleRequest(null, null);

        assertEquals("Data successfully pulled from ElasticSearch and pushed to S3.", result);
    }

    @Test
    public void testHandleRequest_Exception() throws Exception {
    	doReturn(mockAnnConfigAppContext).when(underTest).getAnnotationAppContext();
        when(elasticSearchService.fetchDataFromElasticSearch()).thenThrow(new IOException("Test Exception"));

        String result = underTest.handleRequest(null, null);

        assertTrue(result.startsWith("An error occurred:"));
    }
}