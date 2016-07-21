package com.kelkoo.dojo.bdd.suggestions.context;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;


public class TestGuiceServletConfig {

	GuiceServletConfig config = new GuiceServletConfig();
	Properties properties = config.properties ;
	
	@Test
	public void testLoadProperties() throws Exception {
		assertThat(properties.get("active.countries").toString(), equalTo( "fr,uk" ));
        assertThat(properties.get("search.fr.url").toString(), equalTo( "http://dc1-s6-dev-r410-01.dev.dc1.kelkoo.net:8080/searchsolrnodefr/fr" ));
        assertThat(properties.get("search.uk.url").toString(), equalTo( "http://dc1-s6-dev-r410-01.dev.dc1.kelkoo.net:8085/searchsolrnodeuk/uk" ));
	}

	
	@Test
	public void testActiveCountries() throws Exception {
		assertThat(config.getActiveCountries(), equalTo( new String[] {"fr","uk"} ));
	}

	@Test
	public void testSearchUrl() throws Exception {
		assertThat(config.getSearchUrl("fr"), equalTo( properties.get("search.fr.url").toString() ));
	}

	
}
