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
        assertThat(properties.get("search.ws.url").toString(), equalTo( "http://localhost:8082/search" ));
        assertThat(properties.get("categories.ws.url").toString(), equalTo( "http://localhost:8081/category" ));
        assertThat(properties.get("users.ws.url").toString(), equalTo( "http://localhost:8080/user" ));
	}
	
	
}
