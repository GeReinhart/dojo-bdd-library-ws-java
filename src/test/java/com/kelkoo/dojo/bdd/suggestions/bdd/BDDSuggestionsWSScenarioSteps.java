package com.kelkoo.dojo.bdd.suggestions.bdd;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BDDSuggestionsWSScenarioSteps {

	private static final String SUGGESTIONS_WS_URL_TEMPLATE = "http://localhost:9998/suggestions?userId=%s&maxResults=%s";
	private EmbeddedSuggestionsWSServer server = new EmbeddedSuggestionsWSServer();
	private Client client ;
	
	private User user;

	private ClientResponse wsSuggestionsResponse;

	private UsersWSClient usersWSClientMock;
	private SearchWSClient searchWSClientMock;
	private CategoriesWSClient categoriesWSClientMock;

	@Before
	public void beforeScenario() throws Throwable {
		server.start();
		client = server.client() ;
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
		given_the_user_from_user_ws( this.user.getUserId(), new UserStep(user).fields   );
	}

	@Given("^he is \"([^\"]*)\" years old$")
	public void given_he_is_years_old(Integer age) throws Throwable {
		this.user.setAge(age);
		given_the_user_from_user_ws( this.user.getUserId(), new UserStep(user).fields   );
	}
	
	@Given("^the user from http://localhost:8080/user/([^\"]*)$")
	public void given_the_user_from_user_ws(String userId, List<FieldValue> values) throws Throwable {
		FieldValues fieldsValues = new FieldValues(values);
		this.user = new User(userId , fieldsValues.getAsInteger("age") );
		when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}

	@Given("^he is unknown$")
	public void given_he_is_unknown() throws Throwable {
		given_the_user_from_http_localhost_user_user_return_http_status(user.getUserId(), 404);
	}
	
	@Given("^the user from http://localhost:8080/user/([^\"]*) return http status \"([^\"]*)\"$")
	public void given_the_user_from_http_localhost_user_user_return_http_status(String userId, Integer httpStatus) throws Throwable {
		if ( httpStatus == 404 ){
			when(usersWSClientMock.retrieveUser(userId)).thenThrow(new NotFoundException());
		}
	}
	
	@Given("^the popular categories for this age are$")
	public void given_the_popular_categories_for_this_age_are(List<Category> popularCategoriesGivenAgeUser)
			throws Throwable {
		Boolean isPopular = true ;
		given_the_categories_from_categories_ws(isPopular, user.getAge(), popularCategoriesGivenAgeUser);
	}

	@Given("^the categories from http://localhost:8081/category\\?popular=([^\"]*)&age=(\\d+)$")
	public void given_the_categories_from_categories_ws(Boolean popular , Integer age, List<Category> popularCategoriesGivenAgeUser) throws Throwable {
		when(categoriesWSClientMock.retrieveCategories( popular, user.getAge())).thenReturn(popularCategoriesGivenAgeUser);
	}
	
	
	@Given("^the available books for categories \"([^\"]*)\" are$")
	public void given_the_search_results_for_categories_are(String categoryIds, List<Book> searchResult) throws Throwable {
		Boolean available = true ;
		given_the_books_from_search_ws(categoryIds,available, searchResult);
	}

	@Given("^the books from http://localhost:8082/search\\?categories=([^\"]*)&available=([^\"]*)$")
	public void given_the_books_from_search_ws(String categoryIds, Boolean available, List<Book> searchResult) throws Throwable {
		when(searchWSClientMock.searchBooks(available, categoryIds.split(","))).thenReturn(searchResult);
	}
	
	
	@Given("^the user has already booked the following books$")
	public void given_the_user_has_already_booked_the_following_books(List<Book> alreadyBookedBooks) throws Throwable {
		user.setAlreadyBookedBooks(alreadyBookedBooks);
		when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}

	@When("^we ask for \"([^\"]*)\" suggestions$")
	public void when_we_ask_for_suggestions(Integer maxResults) throws Throwable {
		when_we_call_suggestions_ws( String.format(SUGGESTIONS_WS_URL_TEMPLATE, user.getUserId(), maxResults.toString()) ) ;
	}
	
	@When("^we call ([^\"]*)$")
	public void when_we_call_suggestions_ws(String suggestionsUrl) throws Throwable {
		wsSuggestionsResponse = client.resource(suggestionsUrl).accept("application/xml").get(ClientResponse.class);
	}
	
	@Then("^there is no suggestions$")
	public void then_there_is_no_suggestions() throws Throwable {
		then_the_suggestions_are(new ArrayList<Suggestion>());
	}
	
	@Then("^the suggestions are$")
	public void then_the_suggestions_are(List<Suggestion> expectedSuggestions) throws Throwable {
		if (expectedSuggestions.isEmpty()){
			the_http_code_is(404);	
		}else{
			the_http_code_is(200);
			SuggestionsMarshaller suggestionsMarshaller = new SuggestionsMarshaller();
		    Suggestions actualSuggestions = suggestionsMarshaller.deserialize(wsSuggestionsResponse.getEntity(String.class));
		    checkSameSuggestions(actualSuggestions, expectedSuggestions);
		}
	}
	
	@Then("^the http code is \"([^\"]*)\"$")
	public void the_http_code_is(Integer httpCode) throws Throwable {
		assertThat(wsSuggestionsResponse.getStatus(), is(httpCode));
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

	public static class FieldValue{
		public String field ;
		public String value ;
		
		public FieldValue(String field,String value){
			this.field = field;
			this.value = value;
		}
	}
	
	public static class FieldValues{
		
		private Map<String, String> fieldsValues = new HashMap<String, String>();
		
		public FieldValues( List<FieldValue> fields){
             for (FieldValue fieldValue : fields) {
            	 fieldsValues.put(fieldValue.field, fieldValue.value);
			 } 
		}
		
		public Integer getAsInteger(String field) {
			if(get(field) == null){
				return null ;
			}
			return  Integer.decode(get( field));
		}

		public String get(String field){
			return fieldsValues.get(field);
		}
		
	}
	
	public static class UserStep{
		
		public List<FieldValue> fields = new ArrayList<FieldValue>();
		private String userId;
		private Integer age;
		
		public UserStep(User user){
			this.userId = user.getUserId();
			this.age = user.getAge();
			this.fields.add(new FieldValue("userId",userId));
			if(age != null){
				this.fields.add(new FieldValue("age",age.toString()));
			}
		}
		
		public User toUser(){
			return new User(userId,age);
		}
		
		
	}
	
}
