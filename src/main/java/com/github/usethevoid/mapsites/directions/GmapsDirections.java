package com.github.usethevoid.mapsites.directions;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

public class GmapsDirections implements Directions{
	final static Gson gson = new GsonBuilder().create();
	final GeoApiContext ctx;
	
	public GmapsDirections(String apiKey) {
		ctx = new GeoApiContext();
		ctx.setApiKey(apiKey);
	}
	
	static private String pointStr(Coords p) {
		return String.format("%s,%s", p.lat, p.lon);
	}
	
	static private String[] pointListStrs(List<Coords> points) {
		String[] pointStrs = new String[points.size()];
		int i = 0;
		for (Coords p : points) {
			pointStrs[i++] = pointStr(p);
		}
		return pointStrs;
	}
	
	@Override
	public MapRoute pointsToRoute(Coords start, Coords end, List<Coords> points) {
		DirectionsApiRequest req = DirectionsApi.newRequest(ctx);
		req.origin(pointStr(start));
		req.destination(pointStr(end));
		req.optimizeWaypoints(true);
		req.mode(TravelMode.DRIVING);
		if (points.size() > 0)
			req.waypoints(pointListStrs(points));
		req.alternatives(false);		
		
		MapRoute mapRoute = new MapRoute();
		try {
			DirectionsResult res = req.await();
			DirectionsRoute route = res.routes[0];	
			System.out.println(gson.toJson(route.waypointOrder));
			for (DirectionsLeg leg: route.legs) {
				mapRoute.addSegmentL0(new Coords(leg.startLocation.lat, leg.startLocation.lng), 
						new Coords(leg.endLocation.lat, leg.endLocation.lng),
						leg.duration.inSeconds / 3600.0,
						leg.distance.inMeters / 1000.0);			
						
				for (DirectionsStep step: leg.steps) {
					List<LatLng> path = step.polyline.decodePath();
					mapRoute.addSegmentL1(new Coords(step.startLocation.lat, step.startLocation.lng), 
										new Coords(step.endLocation.lat, step.endLocation.lng),
										step.duration.inSeconds / 3600.0,
										step.distance.inMeters / 1000.0,
										step.htmlInstructions);					
					for(LatLng p: path) 
						mapRoute.addPoint(new Coords(p.lat, p.lng));										
				}
			}
		} catch (Exception e) {
			return null;
		}
		
		return mapRoute;
	}
	
	
	
	
	
}
