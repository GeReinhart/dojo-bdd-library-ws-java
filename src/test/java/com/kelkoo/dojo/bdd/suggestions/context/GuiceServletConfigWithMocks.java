package com.kelkoo.dojo.bdd.suggestions.context;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceServletConfigWithMocks extends GuiceServletConfig {

	public static GuiceServletModuleWithMocks module ;
	
	@Override
	protected Injector getInjector() {
		module = new GuiceServletModuleWithMocks(this);
		return Guice.createInjector(module);
	}
	
	
}