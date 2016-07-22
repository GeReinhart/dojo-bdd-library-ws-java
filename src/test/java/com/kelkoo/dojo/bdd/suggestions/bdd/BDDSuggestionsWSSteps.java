package com.kelkoo.dojo.bdd.suggestions.bdd;

import static org.hamcrest.Matchers.equalTo;
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

	@Given("^the popular categories for this age are$")
	public void given_the_popular_categories_for_this_age_are(List<Category> popularCategoriesGivenAgeUser)
			throws Throwable {
		Boolean isPopular = true ;
		the_categories_from_categories_ws(isPopular, user.getAge(), popularCategoriesGivenAgeUser);
	}

	@Given("^the categories from http://localhost:8081/category\\?popular=([^\"]*)&age=(\\d+)$")
	public void the_categories_from_categories_ws(Boolean isPopular , Integer age, List<Category> popularCategoriesGivenAgeUser) throws Throwable {
		when(categoriesWSClientMock.retrieveCategories( isPopular, user.getAge())).thenReturn(popularCategoriesGivenAgeUser);
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
