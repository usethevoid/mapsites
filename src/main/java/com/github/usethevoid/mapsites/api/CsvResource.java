package com.github.usethevoid.mapsites.api;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.postgresql.geometric.PGpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.app.CsvImporter;
import com.github.usethevoid.mapsites.app.SiteBase;
import com.github.usethevoid.mapsites.app.SiteFinder;
import com.github.usethevoid.mapsites.app.SiteList;
import com.github.usethevoid.mapsites.app.SiteTemplate;
import com.github.usethevoid.mapsites.dao.SiteDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/private/csvimport")
public class CsvResource {
	final static Gson gson = new GsonBuilder().create();
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	final SiteDAO dao;
	final SiteFinder finder;
	final SiteTemplate template;
	
	public CsvResource(SiteDAO dao,SiteFinder finder, SiteTemplate template) {
		this.dao = dao;
		this.template = template;
		this.finder = finder;
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		try {
			String fileName = contentDispositionHeader.getFileName().trim();
			String out = "{}";
			if (fileName.endsWith("csv")) {
				List<SiteBase> list = CsvImporter.parseStream(fileInputStream,template);
				for (SiteBase site: list) {
					int id = dao.insertCompleteWithCoords(site.getPreview(),site.getDetails(),
							site.getCoordsPoint());
					site.setId(id);
					finder.index(site);
				}
				out = gson.toJson(list);
			}
			
			return Response.status(200).entity(out).build();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}

	}
}
