package com.easytesting.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.easytesting.http.HttpConfig;
import com.easytesting.http.HttpMethods;
import com.easytesting.http.EasyHttpClientBuilder;


/**
 * HttpClient类，支持http和https，包括GET,POST,PUT,DELETE方法
 */
public class EasyHttpClient {
	private static final Logger logger = Logger.getLogger(EasyHttpClient.class);
	
	private static  HttpClient httpClient;
	
	private static HttpClient httpsClient;
	
	//初始化http和httpsClient
	static{
		try {
			httpClient = EasyHttpClientBuilder.newInstance().build();
			httpsClient = EasyHttpClientBuilder.newInstance().configSSL().build();
		} catch (Exception e) {
			logger.error("httpClient创建出错： " + e.toString());
		}
	}
	
	/**
	 * 判断url是http还是https，返回相应的默认client对象
	 * 
	 * @return						返回对应默认的client对象
	 * @throws Exception 
	 */
	private static HttpClient create(String url) throws Exception  {
		if(url.toLowerCase().startsWith("https://")){
			return httpsClient;
		}else{
			return httpClient;
		}
	}

	/**
	 * 以Get方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return
	 * @throws Exception
	 */
	public static String get(HttpConfig config) throws Exception {
		return send(config.method(HttpMethods.GET));
	}
	
	/**
	 * 以Post方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return
	 * @throws Exception
	 */
	public static String post(HttpConfig config) throws Exception {
		return send(config.method(HttpMethods.POST));
	}
	
	/**
	 * 以Put方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return
	 * @throws Exception
	 */
	public static String put(HttpConfig config) throws Exception {
		return send(config.method(HttpMethods.PUT));
	}
	
	/**
	 * 以Delete方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return
	 * @throws HttpProcessException
	 */
	public static String delete(HttpConfig config) throws Exception {
		return send(config.method(HttpMethods.DELETE));
	}
	
	
	/**
	 * 请求资源或服务
	 * 
	 * @param config
	 * @return
	 * @throws HttpProcessException
	 */
	public static String send(HttpConfig config) throws Exception {
		return fmt2String(execute(config), config.outenc());
	}
	
	/**
	 * 转化为字符串
	 * 
	 * @param entity			实体
	 * @param encoding	编码
	 * @return
	 * @throws Exception 
	 */
	private static String fmt2String(HttpResponse resp, String encoding) throws Exception {
		String body = "";
		try {
			if (resp.getEntity() != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(resp.getEntity(), encoding);
				logger.debug(body);
			}
			EntityUtils.consume(resp.getEntity());
		} catch (IOException e) {
			logger.error("IO Exception: " + e.toString());
			throw e;
		}finally{			
			close(resp);
		}
		return body;
	}
	
	/**
	 * 请求资源或服务
	 * 
	 * @param client				client对象
	 * @param url					资源地址
	 * @param httpMethod	请求方法
	 * @param parasMap		请求参数
	 * @param headers			请求头信息
	 * @param encoding		编码
	 * @return						返回处理结果
	 * @throws HttpProcessException 
	 */
	private static HttpResponse execute(HttpConfig config) throws Exception {
		if(config.client()==null){//检测是否设置了client
			config.client(create(config.url()));
		}
		HttpResponse resp = null;
		try {
			//创建请求对象
			HttpRequestBase request = getRequest(config.url(), config.method());
			
			//设置header信息
			request.setHeaders(config.headers());
			
			//判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
			if(HttpEntityEnclosingRequestBase.class.isAssignableFrom(request.getClass())){
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				
				//检测url中是否存在参数
				config.url(Utils.checkHasParas(config.url(), nvps, config.inenc()));
				
				//装填参数
				HttpEntity entity = Utils.map2List(nvps, config.map(), config.inenc());
				
				//设置参数到请求对象中
				((HttpEntityEnclosingRequestBase)request).setEntity(entity);
				
				logger.info("请求地址："+config.url());
				if(nvps.size()>0){
					logger.info("请求参数："+nvps.toString());
				}
			}else{
				int idx = config.url().indexOf("?");
				logger.info("请求地址："+config.url().substring(0, (idx>0 ? idx : config.url().length())));
				if(idx>0){
					logger.info("请求参数："+config.url().substring(idx+1));
				}
			}
			//执行请求操作，并拿到结果（同步阻塞）
			resp = (config.context()==null)?config.client().execute(request) : config.client().execute(request, config.context()) ;
			
			if(config.isReturnRespHeaders()){
				//获取所有response的header信息
				config.headers(resp.getAllHeaders());
			}
			
			//获取结果实体
			return resp;
			
		} catch (IOException e) {
			logger.error("IO exception: " + e.toString());
			throw e;
		}
	}
	
	/**
	 * 下载
	 * 
	 * @param client				client对象
	 * @param url					资源地址
	 * @param headers			请求头信息
	 * @param context			http上下文，用于cookie操作
	 * @param out					输出流
	 * @return						返回处理结果
	 * @throws HttpProcessException 
	 */
	public static OutputStream down(HttpClient client, String url, Header[] headers, HttpContext context, OutputStream out) throws Exception {
		return fmt2Stream(execute(HttpConfig.newInstance().client(client).url(url).method(HttpMethods.GET).headers(headers).context(context).out(out)), out);
	}
	
	/**
	 * 下载图片
	 * 
	 * @param config		请求参数配置
	 * @param out					输出流
	 * @return						返回处理结果
	 * @throws HttpProcessException 
	 */
	public static OutputStream down(HttpConfig config) throws Exception {
		return fmt2Stream(execute(config.method(HttpMethods.GET)), config.out());
	}
	
	/**
	 * 转化为流
	 * 
	 * @param entity			实体
	 * @param out				输出流
	 * @return
	 * @throws HttpProcessException 
	 */
	public static OutputStream fmt2Stream(HttpResponse resp, OutputStream out) throws Exception {
		try {
			resp.getEntity().writeTo(out);
			EntityUtils.consume(resp.getEntity());
		} catch (IOException e) {
			logger.error("Exceptoin in method fmt2Stream : " + e.toString());
			throw new IOException(e);
		}finally{
			close(resp);
		}
		return out;
	}
	
	/**
	 * 根据请求方法名，获取request对象
	 * 
	 * @param url					资源地址
	 * @param method			请求方式
	 * @return
	 */
	private static HttpRequestBase getRequest(String url, HttpMethods method) {
		HttpRequestBase request = null;
		switch (method.getName()) {
			case "GET":// HttpGet
				request = new HttpGet(url);
				break;
			case "POST":// HttpPost
				request = new HttpPost(url);
				break;
			case "PUT":// HttpPut
				request = new HttpPut(url);
				break;
			case "DELETE":// HttpDelete
				request = new HttpDelete(url);
				break;
			default:
				request = new HttpPost(url);
				break;
		}
		return request;
	}
	
	/**
	 * 尝试关闭response
	 * 
	 * @param resp				HttpResponse对象
	 */
	private static void close(HttpResponse resp) {
		try {
			if(resp == null) return;
			//如果CloseableHttpResponse 是resp的父类，则支持关闭
			if(CloseableHttpResponse.class.isAssignableFrom(resp.getClass())){
				((CloseableHttpResponse)resp).close();
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
}
