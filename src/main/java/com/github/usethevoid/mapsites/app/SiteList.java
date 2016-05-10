package com.github.usethevoid.mapsites.app;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SiteList {
	@JsonProperty
	final List<SiteBase> sites;
	
	public SiteList(List<SiteBase> sites) {
		this.sites = sites;
	}
	
	public List<SiteBase> getSites() {
		return sites;
	}
	
}
