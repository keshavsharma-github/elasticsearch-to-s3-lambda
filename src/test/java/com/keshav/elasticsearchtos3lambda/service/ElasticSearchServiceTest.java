package com.keshav.elasticsearchtos3lambda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.keshav.elasticsearchtos3lambda.service.beans.Movie;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchServiceTest {
	@InjectMocks
    private ElasticSearchService underTest;

    @Mock
    private ElasticsearchClient elasticSearchClient;

    @Mock
    private QueryBuilderService queryBuilderService;

    @Mock
    private SearchResponse<Movie> mockResponse;

    private final String indexName = "movies";

    @BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(underTest, "indexName", indexName);
	}

	@SuppressWarnings("unchecked")
	@Test
    public void testFetchDataFromElasticSearch_Success() throws IOException, InterruptedException {
        Map<String, String> queryConfig = getMockedQueryConfig();
        List<Query> expectedQueries = new ArrayList<>();
        expectedQueries.add(MatchQuery.of(m -> m.field("field1").query("value1"))._toQuery());
	        
        when(queryBuilderService.buildFilterQueries(eq(queryConfig))).thenReturn(expectedQueries);
        doReturn(mockResponse).when(elasticSearchClient).search(any(Function.class), any());
        SearchResponse<Movie> response = underTest.fetchDataFromElasticSearch();
        assertEquals(mockResponse, response);
    }

    @Test
    public void testFetchDataFromElasticSearch_Exception() throws IOException, InterruptedException {
        Map<String, String> queryConfig = getMockedQueryConfig();
        when(queryBuilderService.buildFilterQueries(eq(queryConfig))).thenThrow(new IllegalArgumentException());
        assertThrows(Exception.class, () -> {
            underTest.fetchDataFromElasticSearch();
        });
    }

	private Map<String, String> getMockedQueryConfig() {
		Map<String, String> queryConfig = new HashMap<>();
        queryConfig.put("director", "Ron Howard");
        queryConfig.put("title", "Rush");
        queryConfig.put("year", "2000");
		return queryConfig;
	}
}