package com.kelkoo.dojo.bdd.suggestions.bdd.en;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(glue = { "com.kelkoo.dojo.bdd.suggestions.bdd.en" }, strict = true, format = {
		"pretty", "html:target/cucumber-bdd.html",
		"json:target/cucumber-bdd.json" }, features = {
		"features/suggestions_en.feature"}, monochrome = true,
        tags = { "@ongoing" }
)
public class OnGoingBDDTest {
	

}
