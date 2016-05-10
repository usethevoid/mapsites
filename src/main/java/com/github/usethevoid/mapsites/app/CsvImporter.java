package com.github.usethevoid.mapsites.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.directions.Coords;

public class CsvImporter {
	static final Logger logger = LoggerFactory.getLogger(CsvImporter.class);
	
	static public Coords getCoords(String coordsStr) {
		if (coordsStr == null)
			return null;		
		Coords coords = new Coords();
		String[] parts = coordsStr.trim().split("[,\\s]+");
		try {
			logger.info(""+parts[0]+"/"+parts[1]);
			coords.lat = Double.parseDouble(parts[0]);
			coords.lon = Double.parseDouble(parts[1]);
		}
		catch (Exception e) {
			return null;
		}
		return coords;
		
	}
	
	static public List<SiteBase> parseStream(InputStream is, SiteTemplate template) throws IOException {
		// Create the CSVFormat object
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
		
		Reader in = new InputStreamReader(is);
		CSVParser parser = new CSVParser(in, format);

		List<SiteBase> sites = new ArrayList<SiteBase>();
		for (CSVRecord record : parser) {
			SiteBase site = new SiteBase();
			Map<String,String> map = record.toMap();
			Set<String> validFields = map.keySet();
			validFields.retainAll(template.getFieldSet());
			
			for(String field: validFields) {
				String value = record.get(field);
				if (value != null)
					site.set(field, value);
			}
				

			site.setCoords(getCoords(record.get("coords")));
			sites.add(site);
		}
		// close the parser
		parser.close();
		
		return sites;
	}
}
