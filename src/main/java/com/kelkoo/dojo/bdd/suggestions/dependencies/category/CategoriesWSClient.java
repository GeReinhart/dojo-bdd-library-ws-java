package com.kelkoo.dojo.bdd.suggestions.dependencies.category;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class CategoriesWSClient {

	private static final Logger LOGGER = Logger.getLogger(CategoriesWSClient.class);

	private String url;
	
	public CategoriesWSClient(String url){
		this.url = url;
	}
	
	public List<Category> retrieveCategories( Boolean isPopular,  Integer  age)  {
		LOGGER.warn("No implementation, return an empty list");
		return new ArrayList<Category>() ;
	}
	

}
