package com.easytesting.lucene.testing;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.easytesting.lucene.SimpleSearchEngine;
import com.easytesting.lucene.SimpleSearchEngine;

public class SimpleSearchEngineTest {
	private SimpleSearchEngine searchEngine;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		searchEngine = SimpleSearchEngine.newInstance();
	}
	
	@Test
	public void testSearch() throws Exception  {
		SimpleSearchEngine.createIndex();
		SimpleSearchEngine.searchResult();
	}
	
	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
	}
}
