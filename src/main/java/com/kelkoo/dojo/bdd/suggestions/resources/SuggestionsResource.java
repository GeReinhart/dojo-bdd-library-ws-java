package com.kelkoo.dojo.bdd.suggestions.resources;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.CategoriesWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.category.Category;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.Book;
import com.kelkoo.dojo.bdd.suggestions.dependencies.search.SearchWSClient;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.User;
import com.kelkoo.dojo.bdd.suggestions.dependencies.user.UsersWSClient;
import com.kelkoo.dojo.bdd.suggestions.representations.Suggestions;
import com.sun.jersey.api.NotFoundException;

@RequestScoped
@Path("/suggestions")
public class SuggestionsResource {

	private static final Logger LOGGER = Logger.getLogger(SuggestionsResource.class);

	private static final Integer DEFAULT_MAX_RESULT = 100;

	private UsersWSClient userWSClient;

	private CategoriesWSClient categoriesWSClient;

	private SearchWSClient searchWSClient;

	@Inject
	public SuggestionsResource(UsersWSClient usersWSClient, SearchWSClient searchWSClient,
			CategoriesWSClient categoriesWSClient) {
		this.userWSClient = usersWSClient;
		this.searchWSClient = searchWSClient;
		this.categoriesWSClient = categoriesWSClient;
	}

//	
//	@GET
//	@Produces("application/xml")
//	public Suggestions getSuggestions(@QueryParam("userId") String userId, @QueryParam("maxResults") Integer maxResults) {
//			return new Suggestions();
//	}	

	
//	
//	@GET
//	@Produces("application/xml")
//	public Suggestions getSuggestions(@QueryParam("userId") String userId, @QueryParam("maxResults") Integer maxResults) {
//
//			Suggestions suggestions = new Suggestions();
//			maxResults = maxResults == null ? DEFAULT_MAX_RESULT : maxResults;
//	
//			User user = userWSClient.retrieveUser(userId);
//			Boolean isPopular = true;
//			List<Category> popularCategories = categoriesWSClient.retrieveCategories(isPopular, user.getAge());
//			Boolean bookAvailable = true;
//			List<Book> books = searchWSClient.searchBooks(bookAvailable, extractCategoryIds(popularCategories));
//	
//			suggestions.addSuggestionsAsBooks(books);
//			return suggestions;
//	}	
	

	@GET
	@Produces("application/xml")
	public Suggestions getSuggestions(@QueryParam("userId") String userId, @QueryParam("maxResults") Integer maxResults) {

		try{
			
			Suggestions suggestions = new Suggestions();
			maxResults = maxResults == null ? DEFAULT_MAX_RESULT : maxResults;
	
			LOGGER.debug("getSuggestions for user " + userId);
	
			User user = userWSClient.retrieveUser(userId);
			Boolean isPopular = true;
			List<Category> popularCategories = categoriesWSClient.retrieveCategories(isPopular, user.getAge());
			Boolean bookAvailable = true;
			List<Book> books = searchWSClient.searchBooks(bookAvailable, extractCategoryIds(popularCategories));
			List<Book> booksForSuggestions = new ArrayList<Book>();
	        Set<String> categories = new HashSet<String>();
	        
			for (Book book : books) {
				if (! user.hasAlreadyBooked(book)  && ! categories.contains(book.getCategoryId()) ) {
					booksForSuggestions.add(book);
					categories.add(book.getCategoryId());
				}
			}
	
			if (booksForSuggestions.size() > maxResults) {
				booksForSuggestions = booksForSuggestions.subList(0, maxResults);
			}
			
			if (booksForSuggestions.size() < maxResults) {
				booksForSuggestions.clear();
				Map<String,Deque<Book>> bookByCategories = new HashMap<String, Deque<Book>>();
				for (Book book : books) {
					if ( !bookByCategories.containsKey(book.getCategoryId()) ){
						bookByCategories.put(book.getCategoryId(), new ArrayDeque<Book>()) ;
					}
					bookByCategories.get(book.getCategoryId()).add(book);
				}
				boolean hasABook = true ;
				while( booksForSuggestions.size() < maxResults &&  booksForSuggestions.size() < books.size() && hasABook ){
					hasABook = false ;
					for (String category : bookByCategories.keySet()  ){
						Book book =  bookByCategories.get(category).pollFirst() ;
						hasABook = book != null || hasABook ; 
						if ( book != null &&  ! user.hasAlreadyBooked(book) ) {
							booksForSuggestions.add(book);
						}
					}
				}
			}
	
			suggestions.addSuggestionsAsBooks(booksForSuggestions);
			LOGGER.debug("Return " + suggestions);
			return suggestions;
			
		}catch(NotFoundException notFoundException){
			throw notFoundException;
		}catch(WebApplicationException e){
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE) ;
		} 
	}

	public String[] extractCategoryIds(List<Category> categories) {
		String[] categoryIds = new String[categories.size()];
		for (int i = 0; i < categoryIds.length; i++) {
			categoryIds[i] = categories.get(i).getCategoryId();
		}
		return categoryIds;
	}

}