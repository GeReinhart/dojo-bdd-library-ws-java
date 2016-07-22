package com.kelkoo.dojo.bdd.suggestions.context;

import com.google.inject.servlet.ServletModule;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UsersWSClient;
import com.kelkoo.dojo.bdd.suggestions.resources.SuggestionsResource;
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
		bindUserWSClient();
		bindSearchWSClient();
		bindCategoriesWSClient();
	}

	private void bindCategoriesWSClient() {
		bind(CategoriesWSClient.class).toInstance(  new CategoriesWSClient(guiceServletConfig.getCategoriesWSUrl())  );
	}

	private void bindSearchWSClient() {
		bind(SearchWSClient.class).toInstance(  new SearchWSClient(guiceServletConfig.getSearchWSUrl())  );
	}
	
	private void bindUserWSClient() {
		bind(UsersWSClient.class).toInstance(  new UsersWSClient(guiceServletConfig.getUsersWSUrl())  );
	}

}