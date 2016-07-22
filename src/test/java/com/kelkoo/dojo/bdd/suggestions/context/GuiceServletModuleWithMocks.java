package com.kelkoo.dojo.bdd.suggestions.context;

import static org.mockito.Mockito.mock;

import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UsersWSClient;

public class GuiceServletModuleWithMocks extends GuiceServletModule {

	public GuiceServletModuleWithMocks(GuiceServletConfig guiceServletConfig) {
		super(guiceServletConfig);
	}

	public UsersWSClient usersWSClientMock = mock(UsersWSClient.class);
	public SearchWSClient searchClientMock = mock(SearchWSClient.class);
	public CategoriesWSClient categoriesWSClientMock = mock(CategoriesWSClient.class);

	@Override
	protected void configureDependenciesBeans() {
		bind(UsersWSClient.class).toInstance(usersWSClientMock);
		bind(SearchWSClient.class).toInstance(searchClientMock);
		bind(CategoriesWSClient.class).toInstance(categoriesWSClientMock);
	}

}
