package com.github.usethevoid.mapsites.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {	
	@JsonProperty
	String title;
	
	@JsonProperty
	String file;
	
	@JsonProperty
	String thumbPath;
	
	@JsonProperty
	String imagePath;
	
	public Image(String title, String file) {
		this.title = title;
		this.file = file;
	}
	
	public void setPaths(String imagePath, String thumbPath) {
		this.imagePath = imagePath;
		this.thumbPath = thumbPath;
	}
	
}
