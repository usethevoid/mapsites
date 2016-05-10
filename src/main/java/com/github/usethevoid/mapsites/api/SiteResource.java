package com.github.usethevoid.mapsites.api;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.postgresql.geometric.PGpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.app.PGgeometryUtils;
import com.github.usethevoid.mapsites.app.SiteBase;
import com.github.usethevoid.mapsites.app.SiteComplete;
import com.github.usethevoid.mapsites.app.SiteFinder;
import com.github.usethevoid.mapsites.app.SitePreview;
import com.github.usethevoid.mapsites.dao.SiteDAO;
import com.github.usethevoid.mapsites.model.SiteDelta;
import com.github.usethevoid.mapsites.model.SiteOp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class SiteResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final static Gson gson = new GsonBuilder().create();
	final SiteDAO dao;
	final SiteFinder finder;
	final GeometryJSON geoJsonConv = new GeometryJSON();
	private static final String EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\","+
			"SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],"+
			"AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,"+
			"AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,"+
			"AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
	CoordinateReferenceSystem worldCRS; 
	
	MathTransform dbTransform;
	public SiteResource(SiteDAO dao,SiteFinder finder) {
		this.dao = dao;
		this.finder = finder;
		try {
			worldCRS = CRS.parseWKT(EPSG4326);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/site/{id}/{unused:.+}")
	public String renderSite(@PathParam("id") Integer id) {
		return "<html><body><b>"+gson.toJson(dao.getSiteCompleteById(id))+"</b>";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/site/{id}")
	public SiteComplete getSiteById(@PathParam("id") Integer id) {
		return dao.getSiteCompleteById(id);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/private/site/{id}")
	public Response updateSite(@PathParam("id") Integer id, SiteDelta delta) {
		Response response = null;
		try {
			SiteComplete orig = dao.getSiteCompleteById(id);
			SiteComplete current = new SiteComplete(orig,delta);
			dao.updateFields(id,current.getPreview(),current.getDetails());
			finder.index(current);
			response = Response.status(201)
		            .header("Location",id)
		            .entity(gson.toJson(new SiteOp(SiteOp.SAVED,id)))
		            .build();
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}
		return response;
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/private/sitearea/{id}")
	public SiteOp updateArea(@PathParam("id") Integer id, String geoJson) {
		try {
			Geometry poly = geoJsonConv.read(geoJson);
			poly.setSRID(4326);
			Point center = poly.getCentroid();
			dao.updateGeometry(id, PGgeometryUtils.pgGeomfromGeom(poly));
			
			dao.updateCoords(id, new PGpoint(center.getY(), center.getX()));
			SiteComplete current = dao.getSiteCompleteById(id);
			finder.index(current);
		} catch (IOException e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			throw new BadRequestException();
		}
		
		return new SiteOp(SiteOp.SAVED,id);
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/private/site/{id}")
	public Response deleteSite(@PathParam("id") Integer id) {
		Response response = null;
		try {
			dao.delete(id);
			finder.delete(Integer.toString(id));
			response = Response.status(200)
		            .entity(gson.toJson(new SiteOp(SiteOp.DELETED,id)))
		            .build();
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}
		return response;
	}
	
	
	@PUT
	@Path("/site")
	@Produces(MediaType.APPLICATION_JSON)
	public Response putSite(SitePreview site) {
		Response response = null;
		try {
			int id = dao.insertPreview(site.getFields());
			site.setId(id);
			finder.index(site);
			response = Response.status(201)
		            .header("Location",id)
		            .entity(gson.toJson(new SiteOp(SiteOp.SAVED,id)))
		            .build();
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}
		return response;
	}
}
