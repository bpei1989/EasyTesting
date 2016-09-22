package com.easytesting.ws;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;


public class EasySoapClient {
	public static final Logger logger = Logger.getLogger(EasySoapClient.class);
	
	private static EasySoapClient easySoapClient;
	
	
	private EasySoapClient() {}
	
	private static JaxWsProxyFactoryBean jaxWsProxyFactoryBean;
	
	public static EasySoapClient newInstance(Class<?> serviceClass, String endpoint) {
		jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
		jaxWsProxyFactoryBean.setServiceClass(serviceClass);
		jaxWsProxyFactoryBean.setAddress(endpoint);
		easySoapClient = new EasySoapClient();
		return easySoapClient;
	}
	
	public static <T> T getWebService() {
		T serviceClient = (T) jaxWsProxyFactoryBean.create();
		return serviceClient;
	}
	
	public static <T> T getWebServiceByHttps() {
		T serviceClient = (T) jaxWsProxyFactoryBean.create();
		TLSClientParameters tlsParams = new TLSClientParameters();
		TrustManager tm = new MyTrustManager();
		TrustManager[] tms = new TrustManager[1];
		tms[0] = tm;
		tlsParams.setTrustManagers(tms);
		tlsParams.setDisableCNCheck(true);
		Client proxy = ClientProxy.getClient(serviceClient);
		((HTTPConduit) proxy.getConduit()).setTlsClientParameters(tlsParams);
		return serviceClient;
	}
	
    private static class MyTrustManager implements  X509TrustManager{
		
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[]{};
		}
		
		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
				String authType) throws java.security.cert.CertificateException {
		}
		
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
				String authType) throws java.security.cert.CertificateException {
		}
	};
}
