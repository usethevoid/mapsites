package com.github.usethevoid.mapsites;

import com.github.usethevoid.mapsites.api.ConfigResource;
import com.github.usethevoid.mapsites.api.CsvResource;
import com.github.usethevoid.mapsites.api.DirectionsResource;
import com.github.usethevoid.mapsites.api.ImageResource;
import com.github.usethevoid.mapsites.api.ListResource;
import com.github.usethevoid.mapsites.api.LoginResource;
import com.github.usethevoid.mapsites.api.ProgressUploadResource;
import com.github.usethevoid.mapsites.api.ReauthResource;
import com.github.usethevoid.mapsites.api.SecretResource;
import com.github.usethevoid.mapsites.api.ShapeResource;
import com.github.usethevoid.mapsites.api.SiteResource;
import com.github.usethevoid.mapsites.app.CsvImporter;
import com.github.usethevoid.mapsites.app.FileStorage;
import com.github.usethevoid.mapsites.app.SiteBase;
import com.github.usethevoid.mapsites.app.SiteFinder;
import com.github.usethevoid.mapsites.app.SiteTemplate;
import com.github.usethevoid.mapsites.auth.BasicAuthenticator;
import com.github.usethevoid.mapsites.auth.TokenAuthenticator;
import com.github.usethevoid.mapsites.auth.TokenHelper;
import com.github.usethevoid.mapsites.dao.ImageDAO;
import com.github.usethevoid.mapsites.dao.SiteDAO;
import com.github.usethevoid.mapsites.directions.Directions;
import com.github.usethevoid.mapsites.directions.GmapsDirections;
import com.github.usethevoid.mapsites.filter.TokenAuthFilter;
import com.github.usethevoid.mapsites.model.FrontendConfig;
import com.github.usethevoid.mapsites.types.YearsField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.DispatcherType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainApplication extends Application<MainConfiguration> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final static Gson gson = new GsonBuilder().create();

	public static void main(String[] args) throws Exception {
		new MainApplication().run(args);
	}

	
	SiteBase readSiteResource(String res) throws IOException {
		InputStream in = this.getClass().getResourceAsStream(res);
		String str = IOUtils.toString(in, "UTF-8");
		Map<String,Object> fields = gson.fromJson(str, Map.class);
		return new SiteBase(null,fields);
	}

	void insertDemoSites(SiteDAO dao, SiteTemplate template, String res) {
		dao.dropTable();
		dao.createTable();
		
		try {
			InputStream is = new FileInputStream(new File(res));
			if (is == null) throw new RuntimeException("Resource not found");
			List<SiteBase> sites = CsvImporter.parseStream(is, template);
			for (SiteBase site : sites) {
				dao.insertCompleteWithCoords(site.getPreview(), site.getDetails(),site.getCoordsPoint());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "mapsites";
	}

	@Override
	public void initialize(Bootstrap<MainConfiguration> bootstrap) {

		AssetsBundle assetsBundle = new AssetsBundle("/assets/", "/app", "index.html");
		bootstrap.addBundle(assetsBundle);
	}

	@Override
	public void run(MainConfiguration config, Environment env) throws UnknownHostException,IOException {
		if (config.getTemplateFile() == null) {
			logger.error("Please set templateFile property in yml");
			return;
		}
		logger.info("Using template from " + config.getTemplateFile());
		final SiteTemplate template = SiteTemplate.readFromFile(config.getTemplateFile());
		if (template == null) 
			throw new RuntimeException("Template not initialized");
		template.registerType(YearsField.TYPE, YearsField.View.class);
		
		

		final SiteFinder siteFinder = new SiteFinder(config.getElasticHost(),config.getElasticPort(), 
				"elasticsearch",config.getElasticSettings());
		siteFinder.init(template);
		siteFinder.clear();
		
		SiteBase.initTemplate(template);
		 
		

		final TokenHelper tokenHelper = new TokenHelper(config.getDomain());
		final BasicAuthenticator passAuth = new BasicAuthenticator();
		final TokenAuthenticator assetsTokenAuth = new TokenAuthenticator(tokenHelper, false);
		final TokenAuthenticator apiTokenAuth = new TokenAuthenticator(tokenHelper, true);
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(env, config.getDataSourceFactory(), "postgresql");
		final SiteDAO siteDao = jdbi.onDemand(SiteDAO.class);
		final ImageDAO imageDao = jdbi.onDemand(ImageDAO.class);
		final FileStorage imageStorage = new FileStorage("./runtime/assets/images","/app/images");
		final FileStorage thumbStorage = new FileStorage("./runtime/assets/thumbs","/app/images");
		
		siteDao.createTable();
		imageDao.createTable();
		insertDemoSites(siteDao,template,config.getTestDataFile());
		
		for (SiteBase site : siteDao.getAll()) {
			siteFinder.index(site);
		}

		env.servlets().addFilter("privateAssets", new TokenAuthFilter(assetsTokenAuth))
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/app/private/*");
		env.servlets().addFilter("privateResources", new TokenAuthFilter(apiTokenAuth))
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/private/*");

		Directions googleDir = new GmapsDirections(config.getGoogleApiKey());
		
		env.jersey().register(MultiPartFeature.class);
		env.jersey().register(new ConfigResource(template, config));
		env.jersey().register(new ListResource(siteFinder));
		env.jersey().register(new SiteResource(siteDao, siteFinder));
		env.jersey().register(new SecretResource());
		env.jersey().register(new LoginResource(tokenHelper, passAuth));
		env.jersey().register(new ReauthResource(tokenHelper, apiTokenAuth));
		env.jersey().register(new ProgressUploadResource());
		env.jersey().register(new ShapeResource());
		env.jersey().register(new ImageResource(imageDao,imageStorage,thumbStorage));
		env.jersey().register(new DirectionsResource(googleDir));
		env.jersey().register(new CsvResource(siteDao, siteFinder, template ));
		
	}

}
