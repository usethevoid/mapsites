package com.github.usethevoid.mapsites.api;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.app.FileStorage;
import com.github.usethevoid.mapsites.dao.ImageDAO;
import com.github.usethevoid.mapsites.model.Image;
import com.github.usethevoid.mapsites.model.ImageList;

@Path("/")
public class ImageResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final FileStorage imageStorage;
	final FileStorage thumbStorage;
	final ImageDAO dao;

	public ImageResource(ImageDAO dao, FileStorage imageStorage, FileStorage thumbStorage) {
		this.imageStorage = imageStorage;
		this.thumbStorage = thumbStorage;
		this.dao = dao;
	}
	
	@POST
	@Path("/private/siteimages/{siteId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@PathParam("siteId") Integer siteId,
			@FormDataParam("imgType") Integer imgType,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		try {
			String fileName = imageStorage.put(fileInputStream, 
					contentDispositionHeader.getFileName(),Integer.toString(siteId));
			dao.insert(fileName, imgType, siteId);
			return Response.status(200).entity(fileName).build();
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}

	}
	
	@GET
	@Path("/siteimages/{siteId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ImageList getSiteImages(@PathParam("siteId") Integer siteId) {
		List<Image> images = dao.getSiteImages(siteId);
		for (Image img: images) 
			img.setPaths(imageStorage.getPath(), thumbStorage.getPath());
		return new ImageList(images);
	}



}
