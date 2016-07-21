package com.kelkoo.dojo.bdd.suggestions.dependencies.category;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;
import com.kelkoo.category.representation.CategoriesItems;
import com.kelkoo.category.representation.CategoryItem;
import com.kelkoo.category.request.CategoriesListRequest;
import com.kelkoo.common.ws.WebServiceCaller;
import com.kelkoo.webservice.client.WebServiceClientException;

@Singleton
public class CategoriesClient extends WebServiceCaller<CategoriesItems> {

	public CategoriesClient(String host, int port) throws WebServiceClientException {
		super(host, port, true, 10000, 1000);
	}

	public List<Category> getSubCategories(String country, String categoryId) throws WebServiceClientException {
		return transform(callWs(new CategoriesListRequest(country, categoryId)));
	}

	private List<Category> transform(CategoriesItems items) {
		List<Category> categories = new ArrayList<Category>();
		for (CategoryItem categoryItem : items.getCategory()) {
			categories.add(new Category(categoryItem.getId().toString(), categoryItem.getName()));
		}
		return categories;
	}

}
