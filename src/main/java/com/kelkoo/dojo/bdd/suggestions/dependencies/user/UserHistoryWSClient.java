package com.kelkoo.dojo.bdd.suggestions.dependencies.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class UserHistoryWSClient {

	private static final Logger LOGGER = Logger.getLogger(UserHistoryWSClient.class);
	
	public UserHistory retrieveUserHistory(String userId) throws NoSuchElementException {
		LOGGER.warn("No implementation, return an empty UserHistory");
		return new UserHistory( userId ) ;
	}

	public List<String> getMostPopularCategories(String country) {
		LOGGER.warn("No implementation, will be mocked");
		return null;
	}

	public void addUserEvent(String user, String userQuery) {
		LOGGER.warn("No implementation, will be mocked");
	}

	public Boolean hasUserHistory(String user) {
		LOGGER.warn("No implementation, will be mocked");
		return null;
	}

	public String getLastQueryForUser(String user) {
		LOGGER.warn("No implementation, will be mocked");
		return null;
	}
	
}
