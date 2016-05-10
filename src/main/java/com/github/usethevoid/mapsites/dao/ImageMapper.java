package com.github.usethevoid.mapsites.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.github.usethevoid.mapsites.app.SiteComplete;
import com.github.usethevoid.mapsites.model.Image;

public class ImageMapper implements ResultSetMapper<Image> {

	// this mapping method will get called for every row in the result set
	public Image  map(int index, ResultSet rs, StatementContext ctx)
			throws SQLException { 
		Image img = new Image(rs.getString("title"),rs.getString("file"));

		return img;
	}
}
