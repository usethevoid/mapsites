package com.github.usethevoid.mapsites;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MainConfiguration extends Configuration {
	final private String domain;
	final private String elasticHost;
	final private String elasticSettings;
	final private String appName;
	final private int elasticPort;
	final private String testDataFile;
	final private String templateFile;
	final private String googleApiKey; 
	final private AppFlags appFlags;
	
	
	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

	@JsonCreator
	public MainConfiguration() {
		this.domain = "localhost";
		this.elasticHost = "localhost";
		this.elasticPort = 8080;
		this.appName = "Sites";
		this.elasticSettings = null;
		this.testDataFile = null;
		this.templateFile = null;
		this.googleApiKey = null;
		this.appFlags = new AppFlags();
	}

	@JsonProperty
	public String getDomain() {
		return domain;
	}
	
	@JsonProperty
	public String getAppName() {
		return appName;
	}
	
	@JsonProperty
	public String getElasticHost() {
		return elasticHost;
	}
	
	@JsonProperty
	public int getElasticPort() {
		return elasticPort;
	}
	
	@JsonProperty
	public String getTestDataFile() {
		return testDataFile;
	}
	
	@JsonProperty
	public String getTemplateFile() {
		return templateFile;
	}
	
	@JsonProperty
	public String getElasticSettings() {
		return elasticSettings;
	}
	
	@JsonProperty
	public String getGoogleApiKey() {
		return googleApiKey;
	}
	
	public AppFlags getAppFlags() {
		return appFlags;
	}

}
