package com.kelkoo.dojo.bdd.suggestions.dependencies.search;

import java.util.Map;

import com.google.inject.Singleton;
import com.kelkoo.search.client.Searcher;
import com.kelkoo.search.client.bean.param.Parameters;
import com.kelkoo.search.client.bean.result.Result;
import com.kelkoo.search.client.exception.SearchServerException;

@Singleton
public class SearchClient {

	Map<String,Searcher> searcherByCountry;

	public SearchClient(Map<String,Searcher> searcherByCountry) {
		this.searcherByCountry = searcherByCountry;
	}

	public Result searchOffers(String country) throws SearchServerException {
		Parameters params = new Parameters();
		return searcherByCountry.get(country).query(params);
	}

	public Result searchOffersByCategory(String country, String categoryId) throws SearchServerException {
		Parameters params = new Parameters();
		params.setCategoryId(Integer.parseInt(categoryId));
		return searcherByCountry.get(country).query(params);
	}

	public Result searchOffersByQuery(String country, String query) throws SearchServerException {
		Parameters params = new Parameters();
		params.setTerms(query);
		return searcherByCountry.get(country).query(params);
	}

}
