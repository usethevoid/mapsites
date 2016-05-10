package com.github.usethevoid.mapsites.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageList {
	
	@JsonProperty
	final List<Image> images;
	
	public ImageList(List<Image> images) {
		this.images = images;
	}
	
	public List<Image> getImages() {
		return images;
	}
}
