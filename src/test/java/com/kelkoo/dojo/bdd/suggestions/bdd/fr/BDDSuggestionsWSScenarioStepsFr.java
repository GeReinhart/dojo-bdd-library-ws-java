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
import cucumber.api.java.fr.Etantdonné;
import cucumber.api.java.fr.Quand;
import cucumber.api.java.fr.Alors;

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
    private List<Book> searchResult ;
	
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

	
	// Etape 1 : Implémenter le cas minimal de niveau 2
	
	@Etantdonné("^l utilisateur depuis le web service http://my.library.com/user/([^\"]*)$")
	public void etant_donne_l_utilisateur_suivant_depuis_le_web_service_utilisateur(String userId, List<FieldValue> values) throws Throwable {
		FieldValues fieldsValues = new FieldValues(values);
		User user = new User(userId,fieldsValues.getAsInteger("age") );
		etant_donne_l_utilisateur_suivant_depuis_le_web_service_utilisateur(userId,user);
	}

	public void etant_donne_l_utilisateur_suivant_depuis_le_web_service_utilisateur(String userId, User user) throws Throwable {
	    this.user = user;
	    when(usersWSClientMock.retrieveUser(user.getUserId())).thenReturn(user);
	}
	
	@Etantdonné("^les catégories depuis le web service http://my.library.com/category\\?popular=([^\"]*)&age=(\\d+)$")
	public void etant_donnee_les_categories_suivante_depuis_le_web_service_categories(Boolean popular , Integer age, List<Category> popularCategoriesGivenAgeUser) throws Throwable {
		when(categoriesWSClientMock.retrieveCategories( popular, user.getAge())).thenReturn(popularCategoriesGivenAgeUser);
	}	
	
	@Etantdonné("^les livres depuis le web service http://my.library.com/search\\?categories=([^\"]*)&available=([^\"]*)$")
	public void etant_donnee_les_livres_suivants_depuis_le_web_service_recherche(String categoryIds, Boolean available, List<Book> searchResult) throws Throwable {
		this.searchResult = searchResult;
		when(searchWSClientMock.searchBooks(available, categoryIds.split(","))).thenReturn(searchResult);
	}		
	
	@Etantdonné("^les livres déja lus depuis le web service http://my.library.com/user/([^\"]*)/books$")
	public void etant_donnee_les_livres_suivants_depuis_le_web_service_utilisateur(String userId,List<Book> alreadyBookedBooks) throws Throwable {
		user.setAlreadyBookedBooks(alreadyBookedBooks);
		when(usersWSClientMock.retrieveUser(userId)).thenReturn(user);
	}	
	
	@Quand("^on appelle ([^\"]*)$")
	public void quand_on_appelle_l_url(String suggestionsUrl) throws Throwable {
		wsSuggestionsResponse = client.resource(suggestionsUrl).accept("application/xml").get(ClientResponse.class);
	}		
	
	@Alors("^le code http retourné est  \"([^\"]*)\"$")
	public void alors_le_code_http_retourne_est(Integer httpCode) throws Throwable {
		assertThat(wsSuggestionsResponse.getStatus(), is(httpCode));
	}
	
	@Alors("^les données retournées sont$")
	public void alors_les_données_retournées_sont(String data) throws Throwable {
		String actualData = wsSuggestionsResponse.getEntity(String.class);
		assertThat(cleanString(actualData), is(cleanString(data)));
	}
	
	//  Etape 2 : Implémenter le cas minimal de niveau 1 correspondant  réutilisation de phrase exécutable de niveau d'abstraction inférieur  

	@Etantdonné("^l utilisateur \"([^\"]*)\"$")
	public void etant_donne_l_utilisateur(String userId) throws Throwable {
		etant_donne_l_utilisateur_suivant_depuis_le_web_service_utilisateur( userId , new User(userId)  );
	}	
	
	@Etantdonné("^il a \"([^\"]*)\" ans$")
	public void etant_donne_l_utilisateur_de_X_ans(Integer age) throws Throwable {
		user.setAge(age);
		etant_donne_l_utilisateur_suivant_depuis_le_web_service_utilisateur( user.getUserId(), user  );
	}	
	
	@Etantdonné("^les catégories populaires pour cet age sont$")
	public void etant_donne_les_categories_populaires_pour_cet_age(List<Category> popularCategoriesGivenAgeUser)	throws Throwable {
		Boolean isPopular = true ;
		etant_donnee_les_categories_suivante_depuis_le_web_service_categories(isPopular, user.getAge(), popularCategoriesGivenAgeUser);
	}
	
	@Etantdonné("^les livres disponibles pour les catégories \"([^\"]*)\" sont$")
	public void etant_donnee_les_livres_disponibles_pour_les_catégories(String categoryIds, List<Book> searchResult) throws Throwable {
		Boolean available = true ;
		etant_donnee_les_livres_suivants_depuis_le_web_service_recherche(categoryIds,available, searchResult);
	}
	
	@Quand("^on demande \"([^\"]*)\" suggestions$")
	public void quand_on_demande_X_suggestions(Integer maxResults) throws Throwable {
		quand_on_appelle_l_url( String.format(SUGGESTIONS_WS_URL_TEMPLATE, user.getUserId(), maxResults.toString()) ) ;
	}	
	
	@Alors("^les suggestions sont$")
	public void alors_les_suggestions_sont(List<Suggestion> expectedSuggestions) throws Throwable {
		// TODO COMMENT
		if (expectedSuggestions.isEmpty()){
			alors_le_code_http_retourne_est(HTTP_404_NOT_FOUND);	
		}else{
			alors_le_code_http_retourne_est(HTTP_200_OK);
		// END COMMENT
		
			SuggestionsMarshaller suggestionsMarshaller = new SuggestionsMarshaller();
		    Suggestions actualSuggestions = suggestionsMarshaller.deserialize(wsSuggestionsResponse.getEntity(String.class));
		    checkSameSuggestions(actualSuggestions, expectedSuggestions);
		}
	}
	
	//  Etape 3 : Implémenter les autres cas nominaux de niveau 1  => réutilisation de phrase exécutable de mếme niveau d abstraction  

	@Etantdonné("^\"([^\"]*)\" livres  sont disponibles pour les catégories populaires pour cet age$")
	public void etant_donnee_X_livres_sont_disponibles_pour_les_categories_populaires_pour_cet_age(int nbBooks) throws Throwable {
		etant_donne_les_categories_populaires_pour_cet_age(asList( new Category("cat1","category1") ));
		List<Book> books = new ArrayList<Book>();
		for (int i = 0; i < nbBooks; i++) {
			books.add( new Book("b1"+i,"book1"+i,"cat1" ) ) ;
		}
		etant_donnee_les_livres_disponibles_pour_les_catégories("cat1", books );
	}
	
	@Alors("^\"([^\"]*)\" suggestions sont proposées parmi les livres précédents$")
	public void alors_X_suggestions_sont_proposees_parmi_les_livres_precedents(Integer nbSuggestions) throws Throwable {
	    Suggestions suggestions = Suggestions.suggestionsFromBooks(  searchResult.subList(0, nbSuggestions) ) ;
	    alors_les_suggestions_sont(suggestions.getSuggestions()) ;
	}	
	
	@Etantdonné("^l utilisateur a déja reservé les livres suivants$")
	public void etant_donnee_l_utilisateur_a_deja_reserve_les_livres_suivants(List<Book> alreadyBookedBooks) throws Throwable {
		etant_donnee_les_livres_suivants_depuis_le_web_service_utilisateur(user.getUserId(),alreadyBookedBooks);
	}	
	
    // Etape 4 : Implémenter les autres cas de niveau 2 pour avoir toutes les phrases exécutables de base 
	
	@Etantdonné("^l utilisateur depuis le web service http://my.library.com/user/([^\"]*) retourne un code http \"([^\"]*)\"$")
	public void etant_donnee_le_status_retourne_par_le_web_service_utilisateur(String userId, Integer httpStatus) throws Throwable {
		if ( httpStatus == HTTP_404_NOT_FOUND ){
			when(usersWSClientMock.retrieveUser(userId)).thenThrow(new NotFoundException());
		}
		// TODO COMMENT
		if ( httpStatus == HTTP_500_INTERNAL_SERVER_ERROR ){
			when(usersWSClientMock.retrieveUser(userId)).thenThrow(new WebApplicationException());
		}
		// END TODO
	}
	
	// Etape 5 : Implémenter les autres cas de niveau 1
	
	@Etantdonné("^il est inconnu$")
	public void etant_donnee_il_est_inconnu() throws Throwable {
		etant_donnee_le_status_retourne_par_le_web_service_utilisateur(user.getUserId(), HTTP_404_NOT_FOUND);
	}	
	
	@Alors("^il n y a pas de suggestions$")
	public void alors_il_n_y_a_pas_de_suggestions() throws Throwable {
		alors_les_suggestions_sont(new ArrayList<Suggestion>());
	}
	
	@Etantdonné("^impossible de récupérer les informations de l utilisateur$")
	public void etant_donnee_impossible_de_recuperer_les_informations_de_l_utilisateur() throws Throwable {
		etant_donnee_le_status_retourne_par_le_web_service_utilisateur(user.getUserId(), HTTP_500_INTERNAL_SERVER_ERROR);
	}	
	
	@Alors("^le système est temporairement indisponible$")
	public void alors_le_systeme_est_temporairement_indisponible() throws Throwable {
		alors_le_code_http_retourne_est(HTTP_503_SERIVCE_UNAVAILABLE) ;
	}	
	
	//  Etape 6 : Implémenter le niveau 0 en générant des données dans les steps 
	
	@Etantdonné("^un utilisateur$")
	public void etant_donne_un_utilisateur() throws Throwable {
		etant_donne_l_utilisateur("userId1");
		etant_donne_l_utilisateur_de_X_ans(4);
		etant_donne_les_categories_populaires_pour_cet_age(asList( new Category("cat1","category1"), new Category("cat2","category2")  ));
		etant_donnee_les_livres_disponibles_pour_les_catégories("cat1,cat2", 
				                                    asList( new Book("b11","book11","cat1" ),
				                                    		new Book("b21","book21","cat2" ),
				                                    		new Book("b31","book31","cat3" )));
	}

	@Quand("^on demande suggestions$")
	public void quand_on_demande_des_suggestions() throws Throwable {
	    quand_on_demande_X_suggestions(3);
	}

	@Alors("^les suggestions proposées sont populaires, disponibles et adaptées à l age de l utilisateur$")
	public void alors_les_suggestions_proposees_sont_populaires_disponibles_et_adaptees_a_l_age_de_l_utilisateur() throws Throwable {
	    alors_les_suggestions_sont(asList( new Suggestion("b11","book11","cat1" ),
				                         new Suggestion("b21","book21","cat2" ),
				                         new Suggestion("b31","book31","cat3" )));
	}
	
	

	/////////////////////////////////////////////////
	// tools
	
	private String cleanString(String in){
		return in.trim().replace("\n", "").replace(" ", "") ;
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
