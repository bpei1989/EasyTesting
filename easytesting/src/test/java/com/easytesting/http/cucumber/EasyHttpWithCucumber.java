package com.easytesting.http.cucumber;

import cucumber.api.junit.Cucumber;
import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		 features =
		 "src/test/resources",
		 glue = "com.easytesting.http.cucumber",
		 plugin = {
		 "pretty",
		 "html:target/cucumber",
		 }
		)
public class EasyHttpWithCucumber {

}
