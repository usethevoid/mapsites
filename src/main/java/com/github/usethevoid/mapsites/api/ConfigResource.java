package com.github.usethevoid.mapsites.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.usethevoid.mapsites.AppFlags;
import com.github.usethevoid.mapsites.MainConfiguration;
import com.github.usethevoid.mapsites.app.SiteTemplate;
import com.github.usethevoid.mapsites.model.FrontendConfig;

@Path("/")
public class ConfigResource {
	final FrontendConfig config = new FrontendConfig();
	
	public ConfigResource(SiteTemplate template, MainConfiguration mainConfig) {
		config.previewFieldsJson = template.getPreviewFieldsJson();
		config.completeFieldsJson = template.getCompleteFieldsJson();
		config.appFlags = mainConfig.getAppFlags();
		config.appName = mainConfig.getAppName();
		config.imageTypes = template.getImageTypes();
		config.mapSearchFieldsJson = template.getSearchFieldsJson(0);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/config")
	public FrontendConfig getConfig() {
		return config;
	}

}
