package com.easytesting.lucene.testing;

import java.util.ArrayList;
import java.util.List;

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
		List<String> lines = new ArrayList<String>();
		EasySearchEngine.createIndex("testcase.log");
		lines = EasySearchEngine.searchResult("Failed to execute workload ");
		System.out.println();
		
		//EasySearchEngine.searchResult("Failed to AND　execute workload　AND GetMaintenanceModeStatus");
	}
	
	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
	}
}
