package com.github.usethevoid.mapsites.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.geotools.geojson.geom.GeometryJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.directions.Directions;
import com.github.usethevoid.mapsites.directions.MapRoute;
import com.github.usethevoid.mapsites.model.Route;
import com.github.usethevoid.mapsites.model.RouteRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/")
public class DirectionsResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final Directions directions;
	final GeometryJSON geoJsonConv = new GeometryJSON();
	final static Gson gson = new GsonBuilder().create();
	
	public DirectionsResource(Directions directions) {
		this.directions = directions;
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/route")
	public Response calculateRoute(RouteRequest req) {
		Response response = null;
		try {
			MapRoute route = directions.pointsToRoute(req.start, req.end, req.waypoints);
			org.joda.time.Duration time = route.getDuration();
			Route out = new Route(route.getSegmentsL0(),route.toGeoJson(),route.getKms(),
					(int)time.getStandardHours(),(int)(time.getStandardMinutes() - time.getStandardHours() * 60) );
			response = Response.status(201)
		            .entity(gson.toJson(out))
		            .build();
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return Response.status(500).build();
		}
		return response;
	}
	

}
