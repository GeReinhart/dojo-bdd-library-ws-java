package com.kelkoo.dojo.bdd.suggestions.resources;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UserHistoryWSClient;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestion;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestions;
import com.kelkoo.search.client.bean.result.Offer;
import com.kelkoo.search.client.bean.result.Result;
import com.kelkoo.search.client.exception.SearchServerException;

@RequestScoped
@Path("/Country/{countryPrefix}/Suggestions")
public class SuggestionsResource {

	private static final Logger LOGGER = Logger.getLogger(SuggestionsResource.class);

	private UserHistoryWSClient userHistoryWSClient;

	private CategoriesClient categoriesClient;

	private SearchClient searchClient;

	@Inject
	public SuggestionsResource(UserHistoryWSClient userHistoryWSClient, SearchClient searchClient,
			CategoriesClient categoriesClient) {
		this.userHistoryWSClient = userHistoryWSClient;
		this.searchClient = searchClient;
		this.categoriesClient = categoriesClient;
	}

	@PathParam("countryPrefix")
	String countryPrefix;

	public void setCountryPrefix(String countryPrefix) {
		this.countryPrefix = countryPrefix;
	}

	@GET
	@QueryParam("maxResults")
	@Produces("application/xml")
	public Suggestions getSuggestions(@QueryParam("user") String user, @QueryParam("userQuery") String userQuery)
			throws SearchServerException {

		Suggestions suggestions = new Suggestions();
		LOGGER.debug("getSuggestions for user " + user + " and query "+ userQuery);
		
		try {
			if (userHistoryWSClient.hasUserHistory(user)) {

				String lastQuery = userHistoryWSClient.getLastQueryForUser(user);
				Result result = searchClient.searchOffersByQuery(countryPrefix, lastQuery);
				suggestions = buildSuggestionsFromResult(result);

			} else {

				List<String> mostPopularCategories = userHistoryWSClient.getMostPopularCategories(countryPrefix);

				Iterator<String> mostPopularCategoriesIterator = mostPopularCategories.iterator();
				while (suggestions.isEmpty() && mostPopularCategoriesIterator.hasNext()) {
					String mostPopularCategory = mostPopularCategoriesIterator.next();
					Result result = searchClient.searchOffersByCategory(countryPrefix, mostPopularCategory);
					suggestions = buildSuggestionsFromResult(result);
				}

			}
		} catch (SearchServerException e) {
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}

		userHistoryWSClient.addUserEvent(user, userQuery);

		
			
		LOGGER.debug("Return " + suggestions);
		return suggestions;
	}

	private Suggestions buildSuggestionsFromResult(Result result) {
		Suggestions suggestions = new Suggestions();
		if (result != null) {
			for (Offer offer : result.offers) {
				suggestions.addSuggestion(new Suggestion(offer.getOfferId(), offer.getTitle()));
			}
		}
		return suggestions;
	}

}