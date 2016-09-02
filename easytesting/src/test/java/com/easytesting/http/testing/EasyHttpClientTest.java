package com.easytesting.http.testing;

import static org.testng.Assert.fail;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.easytesting.http.HttpConfig;
import com.easytesting.http.HttpHeader;
import com.easytesting.http.HttpMethods;
import com.easytesting.http.EasyHttpClientBuilder;
import com.easytesting.http.EasyHttpClient;
import com.easytesting.selenium.WebDriverFactory;


public class EasyHttpClientTest {
	
	private static final Logger log = Logger.getLogger(EasyHttpClientTest.class);
	private String baseUrl;
	private String httpsUrl;
	private String proxyUrl;
	private String response;
	private HttpConfig config = HttpConfig.newInstance();
	
	  @BeforeClass(alwaysRun = true)
	  public void setUp() throws Exception {
	    baseUrl = "http://www.zhihu.com/";
	    httpsUrl = "https://www.baidu.com/";
	    proxyUrl = "https://www.google.com";
	  }

	  @Test
	  public void testSimpleGet() throws Exception {
		response = EasyHttpClient.get(config.url(baseUrl));
	  }

	  @Test
	  public void testHeaderGet() throws Exception {
			Header[] headers=HttpHeader.newInstance().userAgent("Mozilla/5.0").build();
			response = EasyHttpClient.get(config.headers(headers).url(baseUrl));
			log.info("请求结果内容："+ response);
	  }
	  
	  @Test
	  public void testhttpsGet() throws Exception {
			HttpClient client= EasyHttpClientBuilder.newInstance().timeout(20000).configSSL().build();
			response = EasyHttpClient.get(config.client(client).url(httpsUrl));
			log.info("请求结果内容："+ response);
	  }
	  
	  @Test
	  public void testProxyGet() throws Exception {
			HttpClient client= EasyHttpClientBuilder.newInstance().proxy("proxy.vmware.com", 3128).timeout(20000).configSSL().build();
			response = EasyHttpClient.get(config.client(client).url(proxyUrl));
			log.info("请求结果内容："+ response);
	  }
	  @AfterClass(alwaysRun = true)
	  public void tearDown() throws Exception {
		  
	  }
}
