package com.github.usethevoid.mapsites.app;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.github.usethevoid.mapsites.model.FieldFilter;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SiteFilter {
	@JsonProperty
	List<FieldFilter> multiQuery;

	@JsonProperty
	FieldFilter fuzzyQuery;

	@JsonProperty
	String wordQuery;
	
	@JsonProperty
	Integer formIndex = 0;

	public void addPart(String field, String value) {
		if (multiQuery == null)
			multiQuery = new ArrayList<FieldFilter>();
		multiQuery.add(new FieldFilter(field, value));
	}

	public void setFuzzy(String field, String fuzzyValue) {
		reset();
		fuzzyQuery = new FieldFilter(field, fuzzyValue);
	}

	public void setWord(String word) {
		reset();
		wordQuery = word;
	}

	public void reset() {
		fuzzyQuery = null;
		wordQuery = null;
	}
	
	private String[] fieldsToQueryTerms(String[] fields) {
		String[] terms = new String[fields.length];
		int i = 0;
		for (String field: fields) 
			terms[i++] = "fields."+field;
		return terms;
	}

	public QueryBuilder getElasticQuery(SiteTemplate template, int type) {
		if (multiQuery == null && fuzzyQuery == null && wordQuery == null)
			return QueryBuilders.matchAllQuery();

		if (wordQuery != null && wordQuery.length() > 0) {			
			String[] searchFields = fieldsToQueryTerms(template.getSearchFields(type));
			return QueryBuilders.multiMatchQuery(wordQuery, searchFields);
		}

		if (fuzzyQuery != null && fuzzyQuery.value.length() > 0) {			
			System.out.println("fuzzy");
			return QueryBuilders.fuzzyQuery("fields." + fuzzyQuery.field, fuzzyQuery.value);
		}

		if (multiQuery != null && multiQuery.size() > 0) {
			// TODO: take all filter parts into account
			String value = multiQuery.get(0).value;
			String key = multiQuery.get(0).field;

			if (value != null && value.length() > 0 && key != null && key.length() > 0)
				return QueryBuilders.matchQuery("fields." + key, value);
		}

		return QueryBuilders.matchAllQuery();

	}
}
