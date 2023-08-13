package com.keshav.elasticsearchtos3lambda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.keshav.elasticsearchtos3lambda.config.FieldMappingsConfig;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;

@ExtendWith(MockitoExtension.class)
public class QueryBuilderServiceTest {

	@InjectMocks
    private QueryBuilderService underTest;

    @Mock
    private FieldMappingsConfig fieldMappings;

    @Test
    public void testBuildFilterQueries_Success() {
    	when(fieldMappings.getMappings()).thenReturn(getMockedMappings());
        // Test input data
        Map<String, String> queryConfig = new HashMap<>();
        queryConfig.put("field1", "value1");
        queryConfig.put("field2", "42");
        queryConfig.put("field3", "42.2");
        queryConfig.put("field4", "true");
        String field5DateValue = "2023-08-13";
		queryConfig.put("field5", field5DateValue);
		queryConfig.put("field6", "NotPresent");// Test condition where field is not present in mapping

        List<Query> expectedQueries = new ArrayList<>();
        expectedQueries.add(MatchQuery.of(m -> m.field("field1").query("value1"))._toQuery());
        expectedQueries.add(MatchQuery.of(m -> m.field("field2").query(42))._toQuery());
        expectedQueries.add(MatchQuery.of(m -> m.field("field3").query(42.2))._toQuery());
        expectedQueries.add(MatchQuery.of(m -> m.field("field4").query(true))._toQuery());
        LocalDate dateValue = LocalDate.parse(field5DateValue, DateTimeFormatter.ISO_DATE);
        expectedQueries.add(RangeQuery.of(r -> r.field("field5").gte(JsonData.of(dateValue)))._toQuery());
        
        List<Query> actualQueries = underTest.buildFilterQueries(queryConfig);

        assertEquals(expectedQueries.size(), actualQueries.size());
        assertTrue(actualQueries.toString().contains(expectedQueries.get(0).toString()));
        assertTrue(actualQueries.toString().contains(expectedQueries.get(1).toString()));
        assertTrue(actualQueries.toString().contains(expectedQueries.get(2).toString()));
        assertTrue(actualQueries.toString().contains(expectedQueries.get(3).toString()));
        assertTrue(actualQueries.toString().contains(expectedQueries.get(4).toString()));
    }

    @Test
    public void test_UnsupportedField() {
    	when(fieldMappings.getMappings()).thenReturn(getMockedMappings());
    	// Test input data
        Map<String, String> queryConfig = new HashMap<>();
        queryConfig.put("invalidField", "value1");
    	// Test unsupported field type
        assertThrows(IllegalArgumentException.class, () -> 
        	underTest.buildFilterQueries(queryConfig)
        );
    }

    private Map<String, String> getMockedMappings() {
		Map<String, String> mappings = new HashMap<>();
        mappings.put("field1", "string");
        mappings.put("field2", "integer");
        mappings.put("field3", "double");
        mappings.put("field4", "boolean");
        mappings.put("field5", "date");
        mappings.put("invalidField", "unsupportedFieldType");
        return mappings;
	}
}
