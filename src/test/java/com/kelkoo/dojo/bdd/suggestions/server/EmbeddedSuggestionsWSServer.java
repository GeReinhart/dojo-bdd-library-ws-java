package com.kelkoo.dojo.bdd.suggestions.server;

import java.io.StringReader;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.inject.servlet.GuiceFilter;
import com.kelkoo.dojo.bdd.suggestions.context.GuiceServletConfigWithMocks;
import com.kelkoo.dojo.bdd.suggestions.context.GuiceServletModuleWithMocks;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestions;
import com.kelkoo.dojo.bdd.suggestions.representations.SuggestionsMarshaller;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class EmbeddedSuggestionsWSServer extends JerseyTest {

	private static final String APPLICATION_XML = "application/xml";
	private static final String PACKAGE_JERSEY_RESOURCES = "com.kelkoo.dojo.bdd.suggestions.resources";

	private SuggestionsMarshaller suggestionsMarshaller = new SuggestionsMarshaller();
	
	public EmbeddedSuggestionsWSServer() {
		super(new WebAppDescriptor.Builder(PACKAGE_JERSEY_RESOURCES)
				.contextListenerClass(GuiceServletConfigWithMocks.class).filterClass(GuiceFilter.class)
				.contextPath("suggestions").servletPath("/").build());
	}

	public GuiceServletModuleWithMocks mocks() {
		return GuiceServletConfigWithMocks.module;
	}

	public void start() throws Exception {
		super.setUp();
	}

	public void stop() throws Exception {
		super.tearDown();
	}

	public WebResource webResource() {
		return resource();
	}

	public URI getServerURI() {
		return webResource().path("").getURI();
	}

	public ClientResponse clientResponseOnApplicationWadl() {
		return resource().path("application.wadl").accept(APPLICATION_XML).get(ClientResponse.class); 
	}

	public ClientResponse clientResponseOnSuggestions(String relativeUrl, String user, String userQuery) {
		
		
		WebResource webResource = resource().path(relativeUrl);
		if (user != null){
			webResource = webResource.queryParam("user",user)	;
		}
		if (userQuery != null){
			webResource = webResource.queryParam("userQuery",userQuery)	;
		}		
		return webResource.accept(APPLICATION_XML).get(ClientResponse.class);
	}


}
