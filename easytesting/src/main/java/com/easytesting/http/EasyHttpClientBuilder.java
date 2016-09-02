package com.easytesting.http;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.easytesting.http.SSL;


/**
 * HttpClientBuilder实现类，用于简化HttpClient的使用
 * 基本思路：
 * 1.把基于SSL类的ssl socket和普通socket注册到ConnectionSocketFactory中用于产生https和http连接
 * 2.简化连接池，超时，代理的用法
 */
public class EasyHttpClientBuilder extends HttpClientBuilder{
	private boolean isSetPool=false;
	private boolean isSetSSL=false;
	
	private static final int MAX_CONNECTION = 10;
	private static final int MAX_ROUTE = 80;
	
	private SSL ssl = SSL.getInstance();
	
	private EasyHttpClientBuilder(){}
	
	public static EasyHttpClientBuilder newInstance(){
		return new EasyHttpClientBuilder();
	}

	/**
	 * 设置超时时间
	 * 
	 * @param timeout 超时时间，单位-毫秒
	 * @return EasyHttpClientBuilder
	 */
	public EasyHttpClientBuilder timeout(int timeout){
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
		return (EasyHttpClientBuilder) this.setDefaultRequestConfig(config);
	}
	
	/**
	 * 设置ssl安全链接
	 * 
	 * @return
	 * @throws HttpProcessException
	 */
	public EasyHttpClientBuilder configSSL() throws Exception {
		if(isSetPool){//如果已经设置过线程池，那肯定也就是https链接了
			if(isSetSSL){
				throw new Exception("请先设置ssl，后设置pool");
			}
			return this;
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", ssl.getSSLConnectionSocketFactory()).build();
		//设置连接池大小
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		return (EasyHttpClientBuilder) this.setConnectionManager(connManager);
	}
	
	/**
	 * 设置自定义sslcontext
	 * 
	 * @param keyStorePath		密钥库路径
	 * @return
	 * @throws HttpProcessException
	 */
	public EasyHttpClientBuilder configSSL(String keyStorePath) throws Exception{
		return configSSL(keyStorePath,"nopassword");
	}
	/**
	 * 设置自定义sslcontext
	 * 
	 * @param keyStorePath		密钥库路径
	 * @param keyStorepassword		密钥库密码
	 * @return
	 * @throws HttpProcessException
	 */
	public EasyHttpClientBuilder configSSL(String keyStorePath, String keyStorepassword) throws Exception{
		this.ssl = SSL.getInstance().customSSL(keyStorePath, keyStorepassword);
		this.isSetSSL=true;
		return configSSL();
	}
	
	/**
	 * 设置连接池（开启https,默认最大连接数为10，每个路由默认连接数80）
	 * 
	 * @return EasyHttpClientBuilder
	 * @throws Exception
	 */
	public EasyHttpClientBuilder pool() throws Exception{
		return pool(MAX_CONNECTION, MAX_ROUTE);
	}
	
	/**
	 * 设置连接池（开启https）
	 * 
	 * @param maxTotal					最大连接数
	 * @param defaultMaxPerRoute	每个路由默认连接数
	 * @return
	 * @throws HttpProcessException
	 */
	public EasyHttpClientBuilder pool(int maxTotal, int defaultMaxPerRoute) throws Exception{
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", ssl.getSSLConnectionSocketFactory()).build();
		//设置连接池大小
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connManager.setMaxTotal(maxTotal);
		connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
		isSetPool=true;
		return (EasyHttpClientBuilder) this.setConnectionManager(connManager);
	}
	
	/**
	 * 设置代理
	 * 
	 * @param hostOrIP		代理host或者ip
	 * @param port			代理端口
	 * @return
	 */
	public EasyHttpClientBuilder proxy(String hostOrIP, int port){
		// 依次是代理地址，代理端口号，协议类型  
		HttpHost proxy = new HttpHost(hostOrIP, port, "http");  
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		return (EasyHttpClientBuilder) this.setRoutePlanner(routePlanner);
	}
	
}
