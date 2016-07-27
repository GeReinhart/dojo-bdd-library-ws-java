package com.kelkoo.dojo.bdd.suggestions.bdd.fr;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

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

public class BDDSuggestionsWSScenarioStepsFr {

	private static final int HTTP_200_OK = 200;
	private static final int HTTP_404_NOT_FOUND = 404;
	private static final int HTTP_500_INTERNAL_SERVER_ERROR = 500;
	private static final int HTTP_503_SERIVCE_UNAVAILABLE = 503;
	
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
		user = new User();

		usersWSClientMock = server.mocks().usersWSClientMock;
		searchWSClientMock = server.mocks().searchClientMock;
		categoriesWSClientMock = server.mocks().categoriesWSClientMock;
	}

	@After
	public void afterScenario() throws Throwable {
		server.stop();
	}

	
	// Level 0: High Level
	
	@Given("^un utilisateur$")
	public void given_a_user() throws Throwable {
		given_the_user("userId1");
		given_he_is_years_old(4);
		given_the_popular_categories_for_this_age_are(asList( new Category("cat1","category1"), new Category("cat2","category2")  ));
		given_the_search_results_for_categories_are("cat1,cat2", 
				                                    asList( new Book("b11","book11","cat1" ),
				                                    		new Book("b21","book21","cat2" ),
				                                    		new Book("b31","book31","cat3" )));
	}

	@When("^on demande suggestions$")
	public void when_we_ask_for_suggestions() throws Throwable {
	    when_we_ask_for_suggestions(3);
	}

	@Then("^les suggestions proposées sont populaires, disponibles et adaptées à l age de l utilisateur$")
	public void then_the_suggestions_are_popular_and_available_books_adpated_to_the_age_of_the_user() throws Throwable {
	    then_the_suggestions_are(asList( new Suggestion("b11","book11","cat1" ),
				                         new Suggestion("b21","book21","cat2" ),
				                         new Suggestion("b31","book31","cat3" )));
	}
		
	// Level 1: Specifications
	
	@Given("^l utilisateur \"([^\"]*)\"$")
	public void given_the_user(String userId) throws Throwable {
		user.setUserId(userId);
		given_the_user_from_user_ws( this.user.getUserId(), new UserStep(user).fields   );
	}

	@Given("^il a \"([^\"]*)\" ans$")
	public void given_he_is_years_old(Integer age) throws Throwable {
		user.setAge(age);
		given_the_user_from_user_ws( user.getUserId(), new UserStep(user).fields   );
	}
	
	@Given("^il est inconnu$")
	public void given_he_is_unknown() throws Throwable {
		given_the_user_from_user_ws_http_status(user.getUserId(), HTTP_404_NOT_FOUND);
	}
	
	@Given("^impossible de récupérer les informations de l utilisateur$")
	public void impossible_to_get_information_on_the_user() throws Throwable {
		given_the_user_from_user_ws_http_status(user.getUserId(), HTTP_500_INTERNAL_SERVER_ERROR);
	}
	
	@Given("^les catégories populaires pour cet age sont$")
	public void given_the_popular_categories_for_this_age_are(List<Category> popularCategoriesGivenAgeUser)
			throws Throwable {
		Boolean isPopular = true ;
		given_the_categories_from_categories_ws(isPopular, user.getAge(), popularCategoriesGivenAgeUser);
	}

	@Given("^les livres disponibles pour les catégories \"([^\"]*)\" sont$")
	public void given_the_search_results_for_categories_are(String categoryIds, List<Book> searchResult) throws Throwable {
		Boolean available = true ;
		given_the_books_from_search_ws(categoryIds,available, searchResult);
	}

	@Given("^l utilisateur a déja reservé les livres suivants$")
	public void given_the_user_has_already_booked_the_following_books(List<Book> alreadyBookedBooks) throws Throwable {
		given_the_books_from_user_ws(user.getUserId(),alreadyBookedBooks);
	}

	@When("^on demande \"([^\"]*)\" suggestions$")
	public void when_we_ask_for_suggestions(Integer maxResults) throws Throwable {
		when_we_call_suggestions_ws( String.format(SUGGESTIONS_WS_URL_TEMPLATE, user.getUserId(), maxResults.toString()) ) ;
	}
	
	@When("^on appelle ([^\"]*)$")
	public void when_we_call_suggestions_ws(String suggestionsUrl) throws Throwable {
		wsSuggestionsResponse = client.resource(suggestionsUrl).accept("application/xml").get(ClientResponse.class);
	}
	
	@Then("^il n y a pas de suggestions$")
	public void then_there_is_no_suggestions() throws Throwable {
		then_the_suggestions_are(new ArrayList<Suggestion>());
	}
	
	@Then("^les suggestions sont$")
	public void then_the_suggestions_are(List<Suggestion> expectedSuggestions) throws Throwable {
		if (expectedSuggestions.isEmpty()){
			the_http_code_is(HTTP_404_NOT_FOUND);	
		}else{
			the_http_code_is(HTTP_200_OK);
			SuggestionsMarshaller suggestionsMarshaller = new SuggestionsMarshaller();
		    Suggestions actualSuggestions = suggestionsMarshaller.deserialize(wsSuggestionsResponse.getEntity(String.class));
		    checkSameSuggestions(actualSuggestions, expectedSuggestions);
		}
	}

	@Then("^le système est temporairement indisponible$")
	public void the_system_is_temporary_unavaiable() throws Throwable {
		the_http_code_is(HTTP_503_SERIVCE_UNAVAILABLE) ;
	}
	
	// Level 2: Technical details
	
	@Given("^l utilisateur depuis le web service http://my.library.com/user/([^\"]*)$")
	public void given_the_user_from_user_ws(String userId, List<FieldValue> values) throws Throwable {
		FieldValues fieldsValues = new FieldValues(values);
		user.setUserId(userId);
		user.setAge(fieldsValues.getAsInteger("age"));
		when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}

	@Given("^l utilisateur depuis le web service http://my.library.com/user/([^\"]*) retourne un code http \"([^\"]*)\"$")
	public void given_the_user_from_user_ws_http_status(String userId, Integer httpStatus) throws Throwable {
		if ( httpStatus == HTTP_404_NOT_FOUND ){
			when(usersWSClientMock.retrieveUser(userId)).thenThrow(new NotFoundException());
		}
		if ( httpStatus == HTTP_500_INTERNAL_SERVER_ERROR ){
			when(usersWSClientMock.retrieveUser(userId)).thenThrow(new WebApplicationException());
		}
	}
	
	@Given("^les catégories depuis le web service http://my.library.com/category\\?popular=([^\"]*)&age=(\\d+)$")
	public void given_the_categories_from_categories_ws(Boolean popular , Integer age, List<Category> popularCategoriesGivenAgeUser) throws Throwable {
		when(categoriesWSClientMock.retrieveCategories( popular, user.getAge())).thenReturn(popularCategoriesGivenAgeUser);
	}

	@Given("^les livres depuis le web service http://my.library.com/search\\?categories=([^\"]*)&available=([^\"]*)$")
	public void given_the_books_from_search_ws(String categoryIds, Boolean available, List<Book> searchResult) throws Throwable {
		when(searchWSClientMock.searchBooks(available, categoryIds.split(","))).thenReturn(searchResult);
	}	
	
	@Given("^les livres depuis le web service http://my.library.com/user/([^\"]*)/books$")
	public void given_the_books_from_user_ws(String userId,List<Book> alreadyBookedBooks) throws Throwable {
		user.setAlreadyBookedBooks(alreadyBookedBooks);
		when(usersWSClientMock.retrieveUser(userId)).thenReturn(user);
	}
	
	@Then("^le code http retourné est  \"([^\"]*)\"$")
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
