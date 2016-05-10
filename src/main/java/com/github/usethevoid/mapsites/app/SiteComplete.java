package com.github.usethevoid.mapsites.app;

import java.util.Map;

import com.github.usethevoid.mapsites.model.SiteDelta;


public class SiteComplete extends SiteBase {

	public SiteComplete() {
	}
	
	public SiteComplete(SiteBase base, SiteDelta delta) {
		this.id = base.id;
		this.coords = base.coords;
		this.areaGeoJson = base.areaGeoJson;
		for (Map.Entry<String, String> field : base.getFields().entrySet()) {
			this.set(field.getKey(), field.getValue());
		}
		for (Map.Entry<String, String> field : delta.getFields().entrySet()) {
			this.set(field.getKey(), field.getValue());
		}
	}

}
