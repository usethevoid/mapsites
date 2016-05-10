package com.github.usethevoid.mapsites.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SiteFinder {
	final static String INDEX = "sites";
	final static String TYPE = "site";
	
	final Gson gson = new GsonBuilder().create();
	final InetSocketTransportAddress elasticAddress;
	final Settings settings;
	final String settingsFile;
	
	Client client;
	SiteTemplate template;
	CreateIndexRequestBuilder createIndexRequestBuilder;
	
		
	public SiteFinder(String elasticHost, int elasticPort, String clusterName, String settingsFile) throws UnknownHostException {
		elasticAddress = new InetSocketTransportAddress(InetAddress.getByName(elasticHost), elasticPort);
		settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
		this.settingsFile = settingsFile;
	}

	public void init(SiteTemplate template) {
		this.template = template;
		client = TransportClient.builder().settings(settings).build().addTransportAddress(elasticAddress);		
		createIndexRequestBuilder = client.admin().indices().prepareCreate(INDEX);
	}
	
	public void sync() {
		client.admin().indices().prepareRefresh().execute().actionGet();
	}
	
	public void destroy() {
		client.close();
	}

	public void index(SiteBase site) {
		String json = gson.toJson(site);
		System.out.println(json);
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, Long.toString(site.getId()));
		indexRequest.source(json);
		IndexResponse response = client.index(indexRequest).actionGet();
		//System.out.println(response.toString());
	}
	
	public void clear() {
		client.admin().indices().delete(new DeleteIndexRequest(INDEX));
		sync();
		
		try {
			InputStream in = new FileInputStream(new File(settingsFile));
			String json = IOUtils.toString(in, "UTF-8");
			createIndexRequestBuilder.setSource(json);
			createIndexRequestBuilder.execute().actionGet();
			//client.admin().indices().prepareCreate(INDEX).execute().actionGet();
		} catch (IOException e) {
			throw new RuntimeException("Elastic settings resource not found");
		}		
		sync();
	}
	
	public List<SiteBase> search(SiteFilter filter, int type) {
		List<SiteBase> sites = new ArrayList<SiteBase>();
		//QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
		
		SearchResponse response = client.prepareSearch(INDEX)
								        .setTypes(TYPE)
								        .setQuery(filter.getElasticQuery(template,type))
								        .execute()
								        .actionGet();
		SearchHit[] hits = response.getHits().hits();
		
		for (SearchHit hit: hits) {
			Map<String,Object> doc = hit.getSource();
			sites.add(new SiteBase(doc,false));
		}

		return sites;
	}
	
	public List<SiteBase> searchPreviews(SiteFilter filter) {
		List<SiteBase> sites = new ArrayList<SiteBase>();
				
		SearchResponse response = client.prepareSearch(INDEX)
								        .setTypes(TYPE)
								        .setQuery(filter.getElasticQuery(template,filter.formIndex))
								        .setSize(1000)
								        .execute()
								        .actionGet();
		SearchHit[] hits = response.getHits().hits();
		
		for (SearchHit hit: hits) {
			Map<String,Object> doc = hit.getSource();
			sites.add(new SiteBase(doc,true));
		}

		return sites;
	}
	
	public void delete(String id) {
		client.prepareDelete(INDEX,TYPE,id).setRefresh(true).execute();
	}
	
}
