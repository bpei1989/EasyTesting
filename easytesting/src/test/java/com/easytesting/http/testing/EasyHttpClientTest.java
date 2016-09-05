package com.easytesting.http.testing;

import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
import com.easytesting.util.IOUtil;
import com.easytesting.util.FileUtil;


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
	  @Test
	  public void testDownload() throws Exception {
		try {
			String url="http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%8F%A4%E5%85%B8&step_word=&hs=0&pn=3&spn=0&di=73253863210&pi=&rn=1&tn=baiduimagedetail&is=&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=2019014641%2C3081763674&os=2998365926%2C407559684&simid=4206257873%2C564572442&adpicid=0&ln=1963&fr=&fmq=1473043938467_R&fm=detail&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fpic2.ooopic.com%2F12%2F63%2F69%2F59bOOOPICe0_1024.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fojtst_z%26e3B555rtv_z%26e3Bv54AzdH3Fojtst_8dmnmlcl_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0";
			FileUtil.createFileRecursively("E:\\testing\\download.htm");
			FileOutputStream out = new FileOutputStream(new File("E:\\testing\\download.htm"));
			EasyHttpClient.down(HttpConfig.newInstance().url(url).out(out));
			out.flush();
			out.close();
			
			String urlTmall = "https://www.baidu.com/";
			FileUtil.createFileRecursively("E:\\testing\\baidu.htm");
			out = new FileOutputStream(new File("E:\\testing\\baidu.htm"));
			EasyHttpClient.down(HttpConfig.newInstance().url(urlTmall).out(out));
			out.flush();
			out.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  @AfterClass(alwaysRun = true)
	  public void tearDown() throws Exception {
		  
	  }
}
