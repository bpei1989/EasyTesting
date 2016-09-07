package com.easytesting.lucene.testing;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.easytesting.lucene.EasySearchEngine;
import com.easytesting.lucene.EasySearchEngine;

public class SimpleSearchEngineTest {
	private EasySearchEngine searchEngine;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		searchEngine = EasySearchEngine.newInstance();
	}
	
	@Test
	public void testSearch() throws Exception  {
		EasySearchEngine.createIndex("testcase.log");
		//EasySearchEngine.searchResult("ERROR|Test failed");
		EasySearchEngine.searchResult("ERROR AND　Test failed");
	}
	
	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
	}
}
