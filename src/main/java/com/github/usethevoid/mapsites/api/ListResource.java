package com.github.usethevoid.mapsites.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.app.SiteBase;
import com.github.usethevoid.mapsites.app.SiteComplete;
import com.github.usethevoid.mapsites.app.SiteFilter;
import com.github.usethevoid.mapsites.app.SiteFinder;
import com.github.usethevoid.mapsites.app.SiteList;
import com.github.usethevoid.mapsites.app.SiteTemplate;
import com.github.usethevoid.mapsites.dao.SiteDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class ListResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final static Gson gson = new GsonBuilder().create();
	final SiteFinder finder;

	public ListResource(SiteFinder finder) {
		this.finder = finder;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public SiteList findSites(SiteFilter filter) {
		logger.info(gson.toJson(filter));
		List<SiteBase> list = finder.searchPreviews(filter);
		return new SiteList(list);
		
	}
	
	

}
