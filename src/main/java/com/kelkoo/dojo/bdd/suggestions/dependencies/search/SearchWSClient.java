package com.kelkoo.dojo.bdd.suggestions.dependencies.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class SearchWSClient {

	private static final Logger LOGGER = Logger.getLogger(SearchWSClient.class);
	
	private String url;
	
	public SearchWSClient(String url){
		this.url = url;
	}
	
	
	public List<Book> searchBooks(  Boolean bookAvailable,  String... categoryId)  {
		LOGGER.warn("No implementation, return an empty list");
		return new ArrayList<Book>() ;
	}
	

}
