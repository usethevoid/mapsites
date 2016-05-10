package com.github.usethevoid.mapsites.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.github.usethevoid.mapsites.app.SiteComplete;
import com.github.usethevoid.mapsites.model.Image;

public interface ImageDAO {
	  @SqlUpdate("CREATE TABLE IF NOT EXISTS images (id SERIAL PRIMARY KEY, "
	  		+ " tags HSTORE, file TEXT, title TEXT," 
			+ " gallery INT, site INT)")
	  public void createTable();
	  
	  
	  @SqlUpdate("DROP TABLE images")
	  public void dropTable();
	  
	  @SqlUpdate("ALTER TABLE images ADD CONSTRAINT site_fk "
			 + "FOREIGN KEY (site) REFERENCES sites(id) "
			 + "ON DELETE SET NULL")
	  public void addSiteFk();
		  

	  @SqlUpdate("insert into images (file, gallery,site) values (:file, :gallery, :site)")
	  @GetGeneratedKeys
	  public int insert(@Bind("file") String file, @Bind("gallery") int gallery, @Bind("site") int site);

	  @SqlQuery("select file from images where id = :id")
	  public String getImageFileById(@Bind("id") int id);
	  
	  @SqlUpdate("delete from images where id = :id")  
	  public void delete(@Bind("id") int id);
	  
	  @SqlQuery("select * from images where site = :siteId")
	  @RegisterMapper(ImageMapper.class)
	  public List<Image> getSiteImages(@Bind("siteId") int siteId);
	  
	  /**
	   * close with no args is used to close the connection
	   */
	  void close();
}
