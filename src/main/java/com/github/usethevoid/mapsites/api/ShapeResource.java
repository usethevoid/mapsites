package com.github.usethevoid.mapsites.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.wkt.Parser;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Symbolizer;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.app.CsvImporter;
import com.github.usethevoid.mapsites.app.GeoJsonUtils;
import com.github.usethevoid.mapsites.app.KmlUtils;
import com.github.usethevoid.mapsites.directions.Coords;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/")
public class ShapeResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final static Gson gson = new GsonBuilder().create();

	@POST
	@Path("/private/shapeconv")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		try {
			String fileName = contentDispositionHeader.getFileName().trim();
			String out = "{}";
			if (fileName.endsWith("shp")) {
				
			}
			else if (fileName.endsWith("kml") || fileName.endsWith("kmz")) {
				FeatureCollection<SimpleFeatureType,SimpleFeature> features = 
						KmlUtils.kmlFile2FeatureCollection(fileInputStream,fileName);
				out = GeoJsonUtils.firstFeatureToGeoJson(features);
			}
			
			return Response.status(200).entity(out).build();
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}

	}
	
	@POST
	@Path("/private/textconv")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoords(String text) {
		BufferedReader bufReader = new BufferedReader(new StringReader(text));
		String line = null;
		List<Coords> coordsList = new ArrayList<Coords>();
		try {
			while( (line=bufReader.readLine()) != null )
			{
				Coords coords = CsvImporter.getCoords(line);
				coordsList.add(coords);
			}
			String geoJson = GeoJsonUtils.coordsListToGeoJson(coordsList);
			return Response.status(200).entity(geoJson).build();
			
		} catch (IOException e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}
		
		
	}
}
