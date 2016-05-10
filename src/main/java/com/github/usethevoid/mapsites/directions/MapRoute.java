package com.github.usethevoid.mapsites.directions;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.joda.time.Duration;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class MapRoute {
	Geometry pathGeom;
	final List<Coords> pathPoints = new ArrayList<Coords>();
	final List<RouteSegment> segmentsL0 = new ArrayList<RouteSegment>();
	final List<RouteSegment> segmentsL1 = new ArrayList<RouteSegment>();
	
	final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
	final GeometryJSON geoJsonConv = new GeometryJSON();
	
	
	public static class RouteSegment {
		Coords start;
		Coords end;
		double hours;
		double kms;
		String html;		
	}
	
	public MapRoute() {
		
	}
	
	public void addPoint(Coords p) {
		pathPoints.add(p);
	}
	
	public void addSegmentL0(Coords start, Coords end, double hours, double kms) {
		RouteSegment segment = new RouteSegment();
		segment.start = start;
		segment.end = end;
		segment.hours = hours;
		segment.kms = kms;
		segmentsL0.add(segment);
	}
	
	public void addSegmentL1(Coords start, Coords end, double hours, double kms, String html) {
		RouteSegment segment = new RouteSegment();
		segment.start = start;
		segment.end = end;
		segment.hours = hours;
		segment.kms = kms;
		segment.html = html; 
		segmentsL1.add(segment);
	}
	
	
	public Geometry toGeometry() {
		Coordinate[] coords = new Coordinate[pathPoints.size()];
		int i = 0;
		for (Coords p : pathPoints) {
			coords[i++] = new Coordinate(p.lon, p.lat);
		}
		
		Geometry geom = geometryFactory.createLineString(coords);
		return geom;
	}
	
	public String toGeoJson() {
		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

		//set the name
		b.setName( "Feature" );

		//add a geometry property
		b.setCRS( DefaultGeographicCRS.WGS84 ); // set crs first
		b.add( "geometry", LineString.class ); // then add geometry

		//build the type
		final SimpleFeatureType TYPE = b.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
	
		featureBuilder.add(toGeometry());
		SimpleFeature feature = featureBuilder.buildFeature( "fid.1" ); 
		
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal",TYPE);
		featureCollection.add(feature);		
		
		StringWriter buff = new StringWriter();
		
		FeatureJSON json = new FeatureJSON();
		try {
			json.writeFeatureCollection(featureCollection, buff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return buff.toString();
	}
	
	public double getHours() {
		double hours = 0;
		for (RouteSegment s: segmentsL0)
			hours += s.hours;
		return hours;
	}
 	
	public double getKms() {
		double kms = 0;
		for (RouteSegment s: segmentsL0)
			kms += s.kms;
		return kms;
	}
	
	public Duration getDuration() {
		return Duration.standardMinutes((int)(getHours()*60));
	}
	
	public List<RouteSegment> getSegmentsL0() {
		return segmentsL0;
	}

}
