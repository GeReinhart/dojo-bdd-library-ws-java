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

	public String getSearchUrl(String country) {
		return  properties.get( String.format("search.%s.url", country)).toString();
	}
	
	public String getCategoryServingHost() {
		return  properties.get( "category.serving.host").toString();
	}
	
	public int getCategoryServingPort() {
		return  Integer.valueOf( properties.get( "category.serving.port").toString() );
	}

	
	
	
}