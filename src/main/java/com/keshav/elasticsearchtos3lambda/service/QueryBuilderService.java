package com.keshav.elasticsearchtos3lambda.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.util.StringUtils;
import com.keshav.elasticsearchtos3lambda.config.FieldMappingsConfig;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;

@Component
public class QueryBuilderService {
	private final Logger LOGGER = LogManager.getLogger(QueryBuilderService.class);

	@Autowired
	private FieldMappingsConfig fieldMappings;

	public List<Query> buildFilterQueries(Map<String, String> queryConfig) {
		LOGGER.info("buildFilterQuery queryConfig: {}", queryConfig);
		List<Query> allQueries = new ArrayList<>();
		for (Map.Entry<String, String> entry : queryConfig.entrySet()) {
			String fieldName = entry.getKey();
			String fieldValue = entry.getValue();
			String fieldType = fieldMappings.getMappings().get(fieldName);
			if (!StringUtils.isNullOrEmpty(fieldType)) {
				allQueries.add(buildQueryByFieldType(fieldName, fieldValue, fieldType));
			}
		}
		return allQueries;
	}

	/*
	 * Builds Term/Range Query with fieldName and value using fieldType Date Format
	 * must be in "YYYY-MM-DD" Format example "2023-08-01" This method will add
	 * range query from provided date to current date
	 */
	private Query buildQueryByFieldType(String fieldName, String fieldValue, String fieldType) {
		LOGGER.info("buildQueryByFieldType: fieldName:{}, fieldValue: {}, fieldType: {}", fieldName, fieldValue, fieldType);
		switch (fieldType) {
		case "string":
			return MatchQuery.of(m -> m.field(fieldName).query(fieldValue))._toQuery();
		case "integer":
			return MatchQuery.of(m -> m.field(fieldName).query(Integer.parseInt(fieldValue)))._toQuery();
		case "double":
			return MatchQuery.of(m -> m.field(fieldName).query(Double.parseDouble(fieldValue)))._toQuery();
		case "boolean":
			return MatchQuery.of(m -> m.field(fieldName).query(Boolean.parseBoolean(fieldValue)))._toQuery();
		case "date":
			LocalDate dateValue = LocalDate.parse(fieldValue, DateTimeFormatter.ISO_DATE);
			return RangeQuery.of(r -> r.field(fieldName).gte(JsonData.of(dateValue)))._toQuery();
		default:
			throw new IllegalArgumentException("Unsupported field type: " + fieldType);
		}
	}
}