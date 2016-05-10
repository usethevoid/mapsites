package com.github.usethevoid.mapsites.app;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.geojson.geom.GeometryJSON;
import org.postgis.PGgeometry;
import org.postgresql.geometric.PGpoint;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.github.usethevoid.mapsites.directions.Coords;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;

@JsonAutoDetect(fieldVisibility=Visibility.ANY,
getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class SiteBase {
	final static Gson gson = new GsonBuilder().create();
	static SiteTemplate template;

	@JsonProperty
	Integer id;

	@JsonProperty
	final Map<String, Object> fields;

	@JsonProperty
	Coords coords = new Coords();
	
	@JsonProperty
	@JsonInclude(JsonInclude.Include.NON_NULL) 
	String areaGeoJson;

	public static void initTemplate(SiteTemplate template) {
		SiteBase.template = template;
	}

	public SiteBase() {
		fields = new HashMap<String, Object>();
	}
	
	public SiteBase(Integer id, Map<String, Object> fields) {
		this.id = id;
		this.fields = fields;
	}
	
	public SiteBase(Map<String, Object> doc, boolean toPreview) {
		id = (Integer) doc.get("id");
		Map<String, Object> inFields = (Map<String, Object>) doc.get("fields");
		
		if (inFields != null && toPreview) {
			fields = new HashMap<String, Object>();
			for (String key:template.getPreviewFields())
				if (inFields.containsKey(key))
					fields.put(key, inFields.get(key));
		}
		else {
			fields = inFields;
		}
		
		Map<String,Double> coordsMap = (Map<String,Double>) doc.get("coords");
		if (coordsMap != null && coordsMap.containsKey("lat") && 
				coordsMap.containsKey("lon"))
			coords = new Coords(coordsMap.get("lat"),coordsMap.get("lon"));
	}

	public void set(String key, Object value) {
		if (value.getClass().equals(String.class))
			fields.put(key, value);
	}

	public int getId() {
		return id;
	}

	public String getField(String key) {
		Object val = fields.get(key);
		if (val.getClass().equals(String.class))
			return (String) val;
		else
			return gson.toJson(val);
	}

	public Map<String, String> getFields() {
		Map<String, String> pairs = new HashMap<String, String>();
		for (String key : fields.keySet()) {
			pairs.put(key, getField(key));
		}
		return pairs;
	}

	public Map<String, Object> getTree() {
		return fields;
	}

	public Map<String, String> getPreview() {
		Map<String, String> preview = new HashMap<String, String>();
		for (String key : template.getPreviewFields())
			if (fields.containsKey(key))
				preview.put(key, getField(key));
		return preview;
	}

	public Map<String, String> getDetails() {
		Map<String, String> details = new HashMap<String, String>();
		for (String key : template.getDetailsFields())
			if (fields.containsKey(key))
				details.put(key, getField(key));
		return details;
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCoords(Coords coords) {
		this.coords = coords;
	}

	public PGpoint getCoordsPoint() {
		if (coords == null)
			return null;
		return new PGpoint(coords.lon, coords.lat);
	}
	
	public void setAreaPgGeom(PGgeometry area) {
		Geometry geom = PGgeometryUtils.geomFromPgGeom(area);
		GeometryJSON gjson = new GeometryJSON();
		StringWriter sw = new StringWriter();
		try {
			gjson.write(geom, sw);
			this.areaGeoJson = sw.toString();
		} catch (IOException e) {}
	}

}
