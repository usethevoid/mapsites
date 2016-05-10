package com.github.usethevoid.mapsites.app;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.github.usethevoid.mapsites.directions.Coords;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class GeoJsonUtils {
	static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
	
	static public String firstFeatureToGeoJson(FeatureCollection<SimpleFeatureType, SimpleFeature> features) {
		FeatureIterator<SimpleFeature> iterator = features.features();
		GeometryJSON gjson = new GeometryJSON();

		if (iterator.hasNext()) {
			SimpleFeature feature = iterator.next();
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			StringWriter sw = new StringWriter();

			try {
				gjson.write(geom, sw);
			} catch (IOException e) {
				return null;
			}
			return sw.toString();
		}
		return null;
	}
	
	static public String coordsListToGeoJson(List<Coords> coordsList) {
		GeometryJSON gjson = new GeometryJSON();
		
		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

		//set the name
		b.setName( "Feature" );

		//add a geometry property
		b.setCRS( DefaultGeographicCRS.WGS84 ); // set crs first
		b.add( "geometry", LineString.class ); // then add geometry

		//build the type
		final SimpleFeatureType TYPE = b.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		
		Coordinate[] coords = new Coordinate[coordsList.size()];
		int i = 0;
		for (Coords p : coordsList) {
			coords[i++] = new Coordinate(p.lon, p.lat);
		}
		
		Geometry geom = geometryFactory.createLineString(coords);
		
		featureBuilder.add(geom);
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
}
