package com.easytesting.http.testing;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.easytesting.http.HttpConfig;

public class EasyHttpClientBase {
	protected static final Logger log = Logger.getLogger(EasyHttpClientBase.class);
	protected HttpConfig config = HttpConfig.newInstance();
	
	  @BeforeClass(alwaysRun = true)
	  public void setUp() throws Exception {}
	  
	  
	  @AfterClass(alwaysRun = true)
	  public void tearDown() throws Exception {
		  
	  }
	  
	  
}
