package com.kelkoo.dojo.bdd.suggestions.context;

import static org.mockito.Mockito.mock;

import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UserHistoryWSClient;

public class GuiceServletModuleWithMocks extends GuiceServletModule {

	public GuiceServletModuleWithMocks(GuiceServletConfig guiceServletConfig) {
		super(guiceServletConfig);
	}

	public UserHistoryWSClient userHistoryWSClientMock = mock(UserHistoryWSClient.class);
	public SearchClient searchClientMock = mock(SearchClient.class);
	public CategoriesClient categoriesClientMock = mock(CategoriesClient.class);

	@Override
	protected void configureDependenciesBeans() {
		bind(UserHistoryWSClient.class).toInstance(userHistoryWSClientMock);
		bind(SearchClient.class).toInstance(searchClientMock);
		bind(CategoriesClient.class).toInstance(categoriesClientMock);
	}

}
