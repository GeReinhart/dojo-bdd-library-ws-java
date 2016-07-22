package com.kelkoo.dojo.bdd.suggestions.bdd;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.Category;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.Book;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.User;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UsersWSClient;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestion;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestions;
import com.kelkoo.dojo.bdd.suggestions.representations.SuggestionsMarshaller;
import com.kelkoo.dojo.bdd.suggestions.server.EmbeddedSuggestionsWSServer;
import com.sun.jersey.api.client.ClientResponse;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BDDSuggestionsWSSteps {

	private EmbeddedSuggestionsWSServer server = new EmbeddedSuggestionsWSServer();
	private SuggestionsMarshaller suggestionsMarshaller = new SuggestionsMarshaller();

	private User user;

	private ClientResponse clientResponse;

	private UsersWSClient usersWSClientMock;
	private SearchWSClient searchWSClientMock;
	private CategoriesWSClient categoriesWSClientMock;

	@Before
	public void beforeScenario() throws Throwable {
		server.start();
		user = null;

		usersWSClientMock = server.mocks().usersWSClientMock;
		searchWSClientMock = server.mocks().searchClientMock;
		categoriesWSClientMock = server.mocks().categoriesWSClientMock;
	}

	@After
	public void afterScenario() throws Throwable {
		server.stop();
	}

	@Given("^the user \"([^\"]*)\"$")
	public void given_the_user(String userId) throws Throwable {
		this.user = new User(userId);
		when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}

	@Given("^he is \"([^\"]*)\" years old$")
	public void given_he_is_years_old(Integer age) throws Throwable {
		this.user.setAge(age);
		when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}

	@Given("^the popular categories for this age are$")
	public void given_the_popular_categories_for_this_age_are(List<Category> popularCategoriesGivenAgeUser)
			throws Throwable {
		Boolean isPopular = true ;
		when(categoriesWSClientMock.retrieveCategories(isPopular, user.getAge())).thenReturn(popularCategoriesGivenAgeUser);
	}

	@Given("^the available books for categories \"([^\"]*)\" are$")
	public void given_the_search_results_for_categories_are(String categoryIds, List<Book> searchResult) throws Throwable {
		Boolean available = true ;
		when(searchWSClientMock.searchBooks(available, categoryIds.split(","))).thenReturn(searchResult);
	}

	@Given("^the user has already booked the following books$")
	public void given_the_user_has_already_booked_the_following_books(List<Book> alreadyBookedBooks) throws Throwable {
		user.setAlreadyBookedBooks(alreadyBookedBooks);
		when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}

	@When("^we ask for \"([^\"]*)\" suggestions$")
	public void when_we_ask_for_suggestions(Integer maxResults) throws Throwable {
		clientResponse = server.clientResponseOnSuggestions("/Suggestions", user.getUserId(), maxResults);
	}

	@Then("^the suggestions are$")
	public void then_the_suggestions_are(List<Suggestion> expectedSuggestions) throws Throwable {
		Suggestions actualSuggestions = suggestionsMarshaller.deserialize(clientResponse.getEntity(String.class));
		checkSameSuggestions(actualSuggestions, expectedSuggestions);
	}
	
	
	private void checkSameSuggestions(Suggestions actualSuggestions, List<Suggestion> expectedSuggestions) {
		assertThat(actualSuggestions.size(), equalTo(expectedSuggestions.size()));

		for (final Suggestion expectedSuggestion : expectedSuggestions) {

			if (Collections2.filter(actualSuggestions.getSuggestions(), new Predicate<Suggestion>() {
				@Override
				public boolean apply(Suggestion actualSuggestion) {
					return expectedSuggestion.getBookId().equals(actualSuggestion.getBookId());
				}

			}).isEmpty()) {
				fail("Expected " + expectedSuggestion + " not found in " + actualSuggestions);
			}
			;

		}

	}

}
