package com.github.usethevoid.mapsites.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.postgis.PGgeometry;
import org.postgresql.geometric.PGpoint;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.github.usethevoid.mapsites.app.PGgeometryUtils;
import com.github.usethevoid.mapsites.app.SiteComplete;
import com.github.usethevoid.mapsites.app.SitePreview;
import com.github.usethevoid.mapsites.directions.Coords;
import com.github.usethevoid.mapsites.model.Image;

public class SiteCompleteMapper implements ResultSetMapper<SiteComplete> {

	// this mapping method will get called for every row in the result set
	public SiteComplete map(int index, ResultSet rs, StatementContext ctx)
			throws SQLException { 
		SiteComplete site = new SiteComplete();
		site.setId(rs.getInt("id"));
		
		PGpoint coords = (PGpoint)rs.getObject("coords"); 
		if (coords!= null)
			site.setCoords(new Coords(coords));
		
		PGgeometry area = (PGgeometry) rs.getObject("poly");
		if (area != null)
			site.setAreaPgGeom(area);
		
		Map<String, String> preview = (Map<String, String>) rs
				.getObject("preview");
		Map<String, String> details = (Map<String, String>) rs
				.getObject("details");
		
		if (preview == null) return site;
		for (String key : preview.keySet())
			site.set(key, preview.get(key));
		
		if (details == null) return site;
		for (String key : details.keySet())
			site.set(key, details.get(key));

		return site;
	}
}