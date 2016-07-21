package com.kelkoo.dojo.bdd.suggestions.server;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Inspired from https://github.com/jersey/jersey-1.x/tree/master/samples/jersey-guice-filter
 */
public class TestEmbeddedSuggestionsWSServer {

	static EmbeddedSuggestionsWSServer server = new EmbeddedSuggestionsWSServer();

	@BeforeClass
	public static void serverStart() throws Exception {
		server.start();
	}

	@AfterClass
	public static void serverStop() throws Exception {
		server.stop();
	}

	@Test
	public void getNonEmptyApplicationWadl() throws Exception {
		assertThat(server.clientResponseOnApplicationWadl().getStatus(), is(200));
	}

}
