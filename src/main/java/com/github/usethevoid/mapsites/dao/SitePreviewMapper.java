package com.github.usethevoid.mapsites.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.postgresql.geometric.PGpoint;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.github.usethevoid.mapsites.app.SitePreview;
import com.github.usethevoid.mapsites.directions.Coords;

public class SitePreviewMapper implements ResultSetMapper<SitePreview> {

	// this mapping method will get called for every row in the result set
	public SitePreview map(int index, ResultSet rs, StatementContext ctx)
			throws SQLException {

		SitePreview	site = new SitePreview();
		site.setId(rs.getInt("id"));
		
		PGpoint coords = (PGpoint)rs.getObject("coords"); 
		if (coords!= null)
			site.setCoords(new Coords(coords));
		
		Map<String, String> preview = (Map<String, String>) rs
				.getObject("preview");
		for (String key : preview.keySet())
			site.set(key, preview.get(key));

		return site;
	}
}