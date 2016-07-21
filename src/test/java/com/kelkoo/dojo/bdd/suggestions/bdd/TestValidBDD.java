package com.kelkoo.dojo.bdd.suggestions.bdd;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(glue = { "com.kelkoo.dojo.bdd.suggestions.bdd" }, strict = true, format = {
		"pretty", "html:target/cucumber-bdd.html",
		"json:target/cucumber-bdd.json" }, features = {
		"features/suggestions.feature"}, monochrome = true,
        tags = { "@valid" }
)
public class TestValidBDD {

}
