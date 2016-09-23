package com.easytesting.http.cucumber;

import org.apache.http.client.HttpClient;

import com.easytesting.http.EasyHttpClient;
import com.easytesting.http.EasyHttpClientBuilder;
import com.easytesting.http.HttpConfig;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class StepsDef {
	
	private String webSite;
	private HttpConfig config;
	
	@Given("^I have a website")
	public void i_have_a_website() throws Throwable {
		this.webSite = "https://www.tmall.com/";
	}
	
	@When("^I use it to get resource$")
	public void i_use_it_to_get_resource() throws Throwable {
		HttpClient client= EasyHttpClientBuilder.newInstance().timeout(20000).configSSL().build();
		config = HttpConfig.newInstance();
		config.url(webSite);
		config.client(client);
	}
	
	@Then("^I should get it$")
	public void i_should_get_it() throws Throwable {
		String response = EasyHttpClient.get(config);
		System.out.println(response);
	}
}
