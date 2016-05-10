package com.github.usethevoid.mapsites.app;

import java.sql.SQLException;

import org.postgis.PGgeometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class PGgeometryUtils {
	
	static public PGgeometry pgGeomfromGeom(Geometry geom) {
		String wkt = geom.toString();
		PGgeometry pgGeom;
		try {
			pgGeom = new PGgeometry(wkt);
			org.postgis.Geometry pgGeomRaw = pgGeom.getGeometry();
			if (pgGeomRaw != null)
				pgGeomRaw.setSrid(geom.getSRID());
		} catch (SQLException e) {
			return null;
		}
		return pgGeom;
	}
	
	static public Geometry geomFromPgGeom(PGgeometry pgGeom) {
		final WKTReader reader = new WKTReader();
		if (pgGeom == null)
			return null;
		org.postgis.Geometry pgGeomRaw = pgGeom.getGeometry();
		if (pgGeomRaw == null)
			return null;
		try {
			return reader.read(pgGeomRaw.toString());
		} catch (ParseException e) {
			return null;
		}
	}
	
}
