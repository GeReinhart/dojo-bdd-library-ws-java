package com.kelkoo.dojo.bdd.suggestions.bdd;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.kelkoo.dojo.bdd.suggestions.bdd.BDDSuggestionsWSBeans.BDDSearchDocumentBean;
import com.kelkoo.dojo.bdd.suggestions.bdd.BDDSuggestionsWSBeans.BDDSuggestionBean;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UserHistoryWSClient;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestion;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestions;
import com.kelkoo.dojo.bdd.suggestions.representations.SuggestionsMarshaller;
import com.kelkoo.dojo.bdd.suggestions.server.EmbeddedSuggestionsWSServer;
import com.kelkoo.search.client.bean.result.Offer;
import com.kelkoo.search.client.bean.result.Result;
import com.kelkoo.search.client.exception.SearchServerException;
import com.sun.jersey.api.client.ClientResponse;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BDDSuggestionsWSSteps {

	private EmbeddedSuggestionsWSServer server = new EmbeddedSuggestionsWSServer();
	private SuggestionsMarshaller suggestionsMarshaller = new SuggestionsMarshaller();

	private String country;

	private String user;

	private String userQuery;

	private List<String> mostPopularCategories = new ArrayList<>();

	private Map<String, List<BDDSearchDocumentBean>> searchResultByCategory = new HashMap<>();
	private Map<String, List<BDDSearchDocumentBean>> searchResultByQuery = new HashMap<>();

	private ClientResponse clientResponse;
	
	private UserHistoryWSClient userHistoryWSClientMock ;
	private SearchClient searchClientMock;

	@Before
	public void beforeScenario() throws Throwable {
		server.start();
		country = null;
		user = null;
		userQuery = null;
		mostPopularCategories.clear();
		searchResultByCategory.clear();
		clientResponse = null;
		
		userHistoryWSClientMock = server.mocks().userHistoryWSClientMock;
		searchClientMock = server.mocks().searchClientMock;
	}

	@After
	public void afterScenario() throws Throwable {
		server.stop();
	}
	
	
	@Given("^we are working on \"([^\"]*)\"$")
	public void given_we_are_working_on(String country) throws Throwable {

		this.country = country;
	}
	
	@Given("^the user \"([^\"]*)\"$")
	public void given_the_user(String user) throws Throwable {

		this.user = user;
	}	
	
	@Given("^a new user without any query$")
	public void a_new_user_without_any_query() throws Throwable {

		given_the_user("user1");
	    given_the_userHistory_web_service_have_no_history_on(this.user);
		given_the_query_is(null);
	
	}
	
	@Given("^the userHistory web service have no history on \"([^\"]*)\"$")
	public void given_the_userHistory_web_service_have_no_history_on(String user) throws Throwable {

		when(userHistoryWSClientMock.hasUserHistory(user)).thenReturn(false);
	}	
	
	@Given("^the query is \"([^\"]*)\"$")
	public void given_the_query_is(String userQuery) throws Throwable {

		this.userQuery = userQuery;
	}

	@Given("^the most popular category returned by userHistory web service is category \"([^\"]*)\"$")
	public void given_the_most_popular_category_returned_by_userHistory_web_service_is_category(String categoryId)
			throws Throwable {
		
		this.mostPopularCategories = asList(categoryId);
		when(userHistoryWSClientMock.getMostPopularCategories(country)).thenReturn(mostPopularCategories);
	}	

	@Given("^the most popular categories returned by userHistory web service are$")
	public void given_the_most_popular_categories_returned_by_userHistory_web_service_are(List<String> mostPopularCategories)
			throws Throwable {

		this.mostPopularCategories = mostPopularCategories;
		when(userHistoryWSClientMock.getMostPopularCategories(country)).thenReturn(mostPopularCategories);
	}
	
	
	@Given("^the search results for categoryId \"([^\"]*)\" is$")
	public void given_the_search_results_for_categoryId_is(String categoryId, List<BDDSearchDocumentBean> searchDocuments)
			throws Throwable {

		this.searchResultByCategory.put(categoryId, searchDocuments);
		when(searchClientMock.searchOffersByCategory(country, categoryId)).thenReturn(
				buildSearchResultFromSearchDocumentBeans(searchDocuments));
	}	
	
	@Given("^the search results for the query \"([^\"]*)\" are$")
	public void given_the_search_results_for_the_query_are(String userQuery, List<BDDSearchDocumentBean> searchDocuments)
			throws Throwable {

		this.searchResultByQuery.put(userQuery, searchDocuments);
		when(searchClientMock.searchOffersByQuery(country, userQuery)).thenReturn(
				buildSearchResultFromSearchDocumentBeans(searchDocuments));
	}
	
	@Given("^there is no result from the search for categoryId \"([^\"]*)\"$")
	public void given_there_is_no_result_from_the_search_for_categoryId(String categoryId) throws Throwable {
		
		when(searchClientMock.searchOffersByCategory(country, categoryId)).thenReturn(new Result());
	}
	
	@When("^we call suggestions service \"([^\"]*)\"$")
	public void when_we_call_suggestions_service(String relativeUrl) throws Throwable {

		clientResponse = server.clientResponseOnSuggestions(relativeUrl, user, userQuery);
	}
	
	@When("^the suggestion service is called$")
	public void when_the_suggestion_service_is_called() throws Throwable {

		
		String categoryId = "123456";
		given_the_most_popular_category_returned_by_userHistory_web_service_is_category(categoryId);
		given_the_search_results_for_categoryId_is(categoryId, asList(new BDDSearchDocumentBean("o1", categoryId, "offerTitle1", 0.2f)));
		
		given_the_search_results_for_the_query_are(this.userQuery , asList(new BDDSearchDocumentBean("o2", "654321", "offerTitle2", 0.3f)));
		
		when_we_call_suggestions_service("/Country/fr/Suggestions");
	}	
	
	@Then("^the suggestions service call is a success$")
	public void the_suggestions_service_call_is_a_success() throws Throwable {
		
		then_the_response_http_status_is(Response.Status.OK.getStatusCode()) ;
	}
	
	@Then("^the response http status is \"([^\"]*)\"$")
	public void then_the_response_http_status_is(int status) throws Throwable {
		assertThat(clientResponse.getStatus(), is(status));
	}	
	
	@Then("^userHistory web service has been called to create the user \"([^\"]*)\"$")
	public void then_userHistory_web_service_has_been_called_to_create_the_user(String user) throws Throwable {
		
		verify(userHistoryWSClientMock).addUserEvent(user, userQuery);
	}	
	
	@Then("^the suggestions are$")
	public void then_the_suggestions_are(List<BDDSuggestionBean> expectedSuggestions) throws Throwable {
		
		Suggestions actualSuggestions = suggestionsMarshaller.deserialize(clientResponse.getEntity(String.class));
		checkSameSuggestions(actualSuggestions, expectedSuggestions);
	}
	
	
	@Then("^offers from the most popular category are returned$")
	public void then_offers_from_the_most_popular_category_are_returned() throws Throwable {

		List<BDDSearchDocumentBean> bddSearchDocumentBeans = this.searchResultByCategory.get(this.mostPopularCategories.get(0));
		List<BDDSuggestionBean> expectedSuggestions = buildSuggestionsFromSearchDocumentBeans(bddSearchDocumentBeans);

		then_the_suggestions_are(expectedSuggestions);
	}	
	
	@Then("^the user is not a new user anymore$")
	public void the_user_is_not_a_new_user_anymore() throws Throwable {
		
		then_userHistory_web_service_has_been_called_to_create_the_user(this.user);
	}	
	
    

	@Given("^a known user$")
	public void a_known_user() throws Throwable {
		given_the_user("user1");
		given_the_userHistory_web_service_have_history_on(this.user);
	}	

	@Given("^the last query of \"([^\"]*)\" returned by userHistory web service is \"([^\"]*)\"$")
	public void given_the_last_query_of_returned_by_userHistory_web_service_is(String user, String lastUserQuery) throws Throwable {
		when(userHistoryWSClientMock.getLastQueryForUser(user)).thenReturn(lastUserQuery);
	}

	@Given("^the userHistory web service is working properly$")
	public void the_userHistory_web_service_is_working_properly() throws Throwable {
		given_the_most_popular_category_returned_by_userHistory_web_service_is_category("123456");
	}
	
	@Given("^the search is failing$")
	public void given_the_search_is_failing() throws Throwable {
		Throwable searchFailingException = new SearchServerException("The search is failing");
		when(searchClientMock.searchOffers(anyString())).thenThrow(searchFailingException) ;
		when(searchClientMock.searchOffersByCategory(anyString(),anyString())).thenThrow(searchFailingException) ;
		when(searchClientMock.searchOffersByQuery(anyString(),anyString())).thenThrow(searchFailingException) ;
	}

	@Given("^the userHistory web service have history on \"([^\"]*)\"$")
	public void given_the_userHistory_web_service_have_history_on(String user) throws Throwable {
		when(userHistoryWSClientMock.hasUserHistory(user)).thenReturn(true);
	}
	
	@Then("^the suggestions service answer that the service is not available$")
	public void the_suggestions_service_answer_that_the_service_is_not_available() throws Throwable {
		then_the_response_http_status_is(Response.Status.SERVICE_UNAVAILABLE.getStatusCode()) ;
	}
	
	@Then("^offers from his last query are returned$")
	public void then_offers_from_his_last_query_are_returned() throws Throwable {
		
		List<BDDSearchDocumentBean> bddSearchDocumentBeans = this.searchResultByQuery.get(this.userQuery);
		List<BDDSuggestionBean> expectedSuggestions = buildSuggestionsFromSearchDocumentBeans(bddSearchDocumentBeans);
		
		then_the_suggestions_are(expectedSuggestions);
	}
	
	

	private List<BDDSuggestionBean>  buildSuggestionsFromSearchDocumentBeans(List<BDDSearchDocumentBean> bddSearchDocumentBeans) {
		List<BDDSuggestionBean> expectedSuggestions = new ArrayList<>();
		for (BDDSearchDocumentBean bddSearchDocumentBean : bddSearchDocumentBeans) {
			expectedSuggestions.add(new BDDSuggestionBean(country, bddSearchDocumentBean.offerId,
					bddSearchDocumentBean.categoryId, bddSearchDocumentBean.offerTitle));
		}
		return expectedSuggestions ;
	}	
	
	private Result buildSearchResultFromSearchDocumentBeans(List<BDDSearchDocumentBean> searchDocuments) {
		Result result = new Result();

		for (BDDSearchDocumentBean searchDocument : searchDocuments) {
			Offer offer = new Offer();
			offer.setOfferId(searchDocument.offerId);
			if (searchDocument.categoryId != null) {
				offer.setCategoryId(Integer.valueOf(searchDocument.categoryId));
			}
			offer.setTitle(searchDocument.offerTitle);
			if (searchDocument.score != null) {
				offer.setScore(Float.valueOf(searchDocument.score));
			}
			result.offers.add(offer);
		}

		return result;
	}

	private void checkSameSuggestions(Suggestions actualSuggestions, List<BDDSuggestionBean> expectedSuggestions) {
		assertThat(actualSuggestions.size(), equalTo(expectedSuggestions.size()));

		for (final BDDSuggestionBean expectedSuggestion : expectedSuggestions) {

			if (Collections2.filter(actualSuggestions.getSuggestions(), new Predicate<Suggestion>() {
				@Override
				public boolean apply(Suggestion actualSuggestion) {
					return expectedSuggestion.isSameAs(actualSuggestion);
				}

			}).isEmpty()) {
				fail("Expected " + expectedSuggestion + " not found in " + actualSuggestions);
			}
			;

		}

	}

}
