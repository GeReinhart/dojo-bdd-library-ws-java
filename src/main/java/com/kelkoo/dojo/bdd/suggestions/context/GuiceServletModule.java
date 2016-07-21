package com.kelkoo.dojo.bdd.suggestions.context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.servlet.ServletModule;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UserHistoryWSClient;
import com.kelkoo.dojo.bdd.suggestions.resources.SuggestionsResource;
import com.kelkoo.search.client.Searcher;
import com.kelkoo.webservice.client.WebServiceClientException;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletModule extends ServletModule {

	private GuiceServletConfig guiceServletConfig;

	public GuiceServletModule(GuiceServletConfig guiceServletConfig) {
		this.guiceServletConfig = guiceServletConfig;
	}

	@Override
	protected void configureServlets() {
		configureInternalBeans();
		configureDependenciesBeans();
		serve("/*").with(GuiceContainer.class);
	}
	
	protected void configureInternalBeans(){
		bind(SuggestionsResource.class);
	}

	protected void configureDependenciesBeans(){
		bind(UserHistoryWSClient.class);
		bindSearchClient();
		bindCategoriesClient();
	}

	private void bindCategoriesClient() {
		try {
			bind(CategoriesClient.class).toInstance(  new CategoriesClient(guiceServletConfig.getCategoryServingHost(),guiceServletConfig.getCategoryServingPort())  );
		} catch (WebServiceClientException e) {
			throw new RuntimeException(e);
		}
	}

	private void bindSearchClient() {
		Map<String, Searcher> searcherByCountry = new HashMap<String, Searcher>();
		for (String country : guiceServletConfig.getActiveCountries()) {
			SearchClient searchClient = new SearchClient(searcherByCountry ) ;
			bind(SearchClient.class).toInstance(searchClient);
			try {
				searcherByCountry.put(country, new Searcher(new URL(guiceServletConfig.getSearchUrl(country)))) ;
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}