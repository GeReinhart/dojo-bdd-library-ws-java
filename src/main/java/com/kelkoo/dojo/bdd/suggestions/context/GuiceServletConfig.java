package com.kelkoo.dojo.bdd.suggestions.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfig extends GuiceServletContextListener {

	final Properties properties = new Properties();
	
	public GuiceServletConfig()  {
		super();
		loadProperties();
	}
	
	private void loadProperties()  {
		try (final InputStream stream = this.getClass().getResourceAsStream("/suggestionsws.properties")) {
			properties.load(stream);
		} catch (IOException e) {
            throw new RuntimeException(e);
		}		
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new GuiceServletModule(this));
	}

	public Properties getProperties() {
		return properties;
	}

	public String[] getActiveCountries() {
		return properties.get("active.countries").toString().split(",");
	}

	public String getSearchWSUrl() {
		return  properties.get( "search.ws.url").toString();
	}
	
	public String getCategoriesWSUrl() {
		return  properties.get( "categories.ws.url").toString();
	}
	
	public String getUsersWSUrl() {
		return  properties.get( "users.ws.url").toString();
	}
	
	
	
}