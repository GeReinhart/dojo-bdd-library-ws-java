package com.kelkoo.dojo.bdd.suggestions.bdd.fr;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(glue = { "com.kelkoo.dojo.bdd.suggestions.bdd.fr" }, strict = true, format = {
		"pretty", "html:target/cucumber-bdd-fr.html",
		"json:target/cucumber-bdd-fr.json" }, features = {
		"features/suggestions_fr.feature"}, monochrome = true,
        tags = { "@ongoing" }
)
public class OnGoingBDDFrTest {
	

}
