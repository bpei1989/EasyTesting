package com.easytesting.ws;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.easytesting.http.EasyHttpClient;
import com.easytesting.http.EasyHttpClientBuilder;
import com.easytesting.http.HttpConfig;
import com.easytesting.http.HttpConstants;
import com.easytesting.http.HttpHeader;
import com.easytesting.util.StringUtil;

/**
 * Rest客户端，封装常用的REST方法,基于EasyHttpClient
 */
public class EasyHttpRestClient {

    private static final Logger logger = Logger.getLogger(EasyHttpRestClient.class);

	private static int timeout = 20000;
    private static int maxTotal = 200;
    private static int maxPerRoute = 30;
    private static int retryCount = 3;


	private static HttpConfig config = HttpConfig.newInstance();
    
    private EasyHttpRestClient() {}
    

    public static JSONArray queryJSONArray(String url){
    	try {
	    	HttpClient client = EasyHttpClientBuilder.newInstance().timeout(timeout).pool(maxTotal, maxPerRoute).build();
	        String body = EasyHttpClient.get(config.client(client).url(url));
	        if (!StringUtil.isBlank(body)) {
	            return JSONArray.parseArray(body);
	        }
    	} catch (Exception e) {
    		logger.error("", e);
        }
        return null;
    }
    
    public static JSONArray queryJSONArrayByHttps(String url) {
    	try {
    		HttpClient client = EasyHttpClientBuilder.newInstance().configSSL().timeout(timeout).pool(maxTotal, maxPerRoute).build();
    		String body = EasyHttpClient.get(config.client(client).url(url));
	        if (!StringUtil.isBlank(body)) {
	            return JSONArray.parseArray(body);
	        }
    	} catch (Exception e) {
        	logger.error("", e);
        }
        return null;
    }
    
    public static JSONObject queryJSONObject(String url){
    	try {
	    	HttpClient client = EasyHttpClientBuilder.newInstance().timeout(timeout).pool(maxTotal, maxPerRoute).build();
	    	String body = EasyHttpClient.get(config.client(client).url(url));
	        if (!StringUtil.isBlank(body)) {
	            return JSONObject.parseObject(body);
	        }
        } catch (Exception e) {
        	logger.error("", e);
        }
    	return null;
    }


    public static JSONObject queryJSONObjectWithRetry(String url){
    	int count = 0;
        JSONObject jsonObject = queryJSONObject(url);
        while (jsonObject == null && retryCount >= count) {
        	count++;
            jsonObject = queryJSONObject(url);
            
            logger.warn("第" + count + "次重试...");

            if (jsonObject == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("中断异常： ", e);
                }
            }
        }
        return jsonObject;
    }

    public static JSONObject queryJSONObjectByHttps(String url) {
    	try {
    		HttpClient client = EasyHttpClientBuilder.newInstance().configSSL().timeout(timeout).pool(maxTotal, maxPerRoute).build();
    		String body = EasyHttpClient.get(config.client(client).url(url));
	        if (!StringUtil.isBlank(body)) {
	            return JSONObject.parseObject(body);
	        }
    	} catch (Exception e) {
        	logger.error("", e);
        }
        return null;
    }

    public static boolean postToUrlByHttps(String body, String url) {
    	try {
    		HttpClient client = EasyHttpClientBuilder.newInstance().configSSL().timeout(timeout).pool(maxTotal, maxPerRoute).build();
    		Header[] headers=HttpHeader.newInstance().userAgent("Mozilla/5.0").connection(HttpConstants.KEEP_ALIVE).contentType(HttpConstants.APPLICATION_JSON).build();
    		String ret = EasyHttpClient.post(config.client(client).headers(headers).url(url));
	        if (!StringUtil.isBlank(ret)) {
	            return true;
	        }
    	} catch (Exception e) {
        	logger.error("", e);
        }
        return false;
    }
    
    public static boolean postToUrlBy(String body, String url) {
    	try {
    		HttpClient client = EasyHttpClientBuilder.newInstance().timeout(timeout).pool(maxTotal, maxPerRoute).build();
    		Header header1 = new BasicHeader("Content-Type", HttpConstants.APPLICATION_JSON);
    		Header header2 = new BasicHeader("connection", HttpConstants.KEEP_ALIVE);
    		Header[] headers = {header1, header2};
    		String ret = EasyHttpClient.post(config.client(client).headers(headers).url(url));
	        if (!StringUtil.isBlank(ret)) {
	            return true;
	        }
    	} catch (Exception e) {
        	logger.error("", e);
        }
        return false;
    }
    
    public static String getContentFromHtml(String url, String selectId) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(selectId);
        return elements.toString();
    }
    
    public static int getTimeout() {
		return timeout;
	}

	public static void setTimeout(int timeout) {
		EasyHttpRestClient.timeout = timeout;
	}

	public static int getMaxTotal() {
		return maxTotal;
	}

	public static void setMaxTotal(int maxTotal) {
		EasyHttpRestClient.maxTotal = maxTotal;
	}

	public static int getMaxPerRoute() {
		return maxPerRoute;
	}

	public static void setMaxPerRoute(int maxPerRoute) {
		EasyHttpRestClient.maxPerRoute = maxPerRoute;
	}

    public static int getRetryCount() {
		return retryCount;
	}

	public static void setRetryCount(int retryCount) {
		EasyHttpRestClient.retryCount = retryCount;
	}
	
}

