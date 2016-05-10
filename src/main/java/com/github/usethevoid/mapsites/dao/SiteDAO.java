package com.github.usethevoid.mapsites.dao;

import java.util.List;
import java.util.Map;

import org.postgis.PGgeometry;
import org.postgresql.geometric.PGpoint;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.github.usethevoid.mapsites.app.SiteComplete;
import com.github.usethevoid.mapsites.app.SitePreview;

public interface SiteDAO {
	@SqlUpdate("CREATE TABLE IF NOT EXISTS sites (id SERIAL PRIMARY KEY, "
			+ " preview HSTORE, details HSTORE, coords POINT, poly GEOMETRY)")
	public void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS sites")
	public void dropTable();

	@SqlUpdate("insert into sites (preview,details,coords) " + 
				"values (:preview, :details, :coords)")
	@GetGeneratedKeys
	public int insertCompleteWithCoords(@Bind("preview") Map<String, String> preview,
			@Bind("details") Map<String, String> details, @Bind("coords") PGpoint coords);
	
	@SqlUpdate("insert into sites (preview,details) " +
				"values (:preview, :details)")
	@GetGeneratedKeys
	public int insertComplete(@Bind("preview") Map<String, String> preview,
			@Bind("details") Map<String, String> details);

	@SqlUpdate("insert into sites (preview) values (:preview)")
	@GetGeneratedKeys
	public int insertPreview(@Bind("preview") Map<String, String> preview);

	@SqlUpdate("update sites set details=:details where id=:id")
	public void updateDetails(@Bind("id") int id, @Bind("details") Map<String, String> details);
	
	@SqlUpdate("update sites set poly=:poly where id=:id")
	public void updateGeometry(@Bind("id") int id, @Bind("poly") PGgeometry poly);
	
	@SqlUpdate("update sites set coords=:coords where id=:id")
	public void updateCoords(@Bind("id") int id, 
			@Bind("coords") PGpoint coords);

	@SqlUpdate("update sites set details=:details,preview=:preview where id=:id")
	public void updateFields(@Bind("id") int id, @Bind("preview") Map<String, String> preview,
			@Bind("details") Map<String, String> details);

	@SqlUpdate("delete from sites where id = :id")
	public void delete(@Bind("id") int id);

	@SqlQuery("select * from sites where id = :id")
	@RegisterMapper(SitePreviewMapper.class)
	public SitePreview getSitePreviewById(@Bind("id") int id);

	@SqlQuery("select * from sites where id = :id")
	@RegisterMapper(SiteCompleteMapper.class)
	public SiteComplete getSiteCompleteById(@Bind("id") int id);

	@SqlQuery("select * from sites")
	@RegisterMapper(SiteCompleteMapper.class)
	public List<SiteComplete> getAll();
	
	@SqlQuery("select coords from sites where id=:id")
	public String getCoords(@Bind("id") int id);

	/**
	 * close with no args is used to close the connection
	 */
	public void close();
}
