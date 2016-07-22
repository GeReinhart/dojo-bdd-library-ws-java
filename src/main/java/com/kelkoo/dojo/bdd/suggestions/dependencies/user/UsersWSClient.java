package com.kelkoo.dojo.bdd.suggestions.dependencies.user;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class UsersWSClient {

	private static final Logger LOGGER = Logger.getLogger(UsersWSClient.class);
	
	private String url;
	
	public UsersWSClient(String url){
		this.url = url;
	}	
	
	public User retrieveUser(String userId)  {
		LOGGER.warn("No implementation, return an empty User");
		return new User( userId ) ;
	}

	public Boolean hasUser(String user) {
		LOGGER.warn("No implementation, will be mocked");
		return null;
	}
	
}
