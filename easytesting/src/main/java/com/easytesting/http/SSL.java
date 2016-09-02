package com.easytesting.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;


/*
 * SSL相关操作类
 * 基本思路是：
 * 1.重写X509TrustManager和HostnameVerifier方法，使客户端信任服务器证书
 * 2.通过SSLContext设置SSL的算法，证书等上下文
 * 3.通过SSLConnectionSocketFactory（基于SSLContext和TrustManager）产生ssl socket
 * */
public class SSL {
	private static final TrustManagerAndVerifier trustManagerAndVerifier = new TrustManagerAndVerifier();
	private static SSLConnectionSocketFactory sslConnFactory ;
	private SSLContext sslContext;
	
	public static SSL getInstance(){
		return new SSL();
	}
	
    public static HostnameVerifier getTrustManagerAndVerifier() {
        return trustManagerAndVerifier;
    }
	
    // 重写X509TrustManager类的三个方法,信任服务器证书
    private static class TrustManagerAndVerifier implements  X509TrustManager, HostnameVerifier{
		
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

		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}
	};
    
    public synchronized SSLConnectionSocketFactory getSSLConnectionSocketFactory() throws NoSuchAlgorithmException, KeyManagementException  {
    	if (sslConnFactory != null)
    		return sslConnFactory;

    	SSLContext sc = getSSLContext();
    	sc.init(null, new TrustManager[] { trustManagerAndVerifier }, new java.security.SecureRandom());
    	sslConnFactory = new SSLConnectionSocketFactory(sc, trustManagerAndVerifier);

    	return sslConnFactory;
    }
    
    public SSLContext getSSLContext() throws NoSuchAlgorithmException{
		if(sslContext == null){
			sslContext = SSLContext.getInstance("SSLv3");
		}
		return sslContext;
    }
    
    public SSL customSSL(String keyStorePath, String keyStorepassword){
    	FileInputStream instream =null;
    	KeyStore trustStore = null; 
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			instream = new FileInputStream(new File(keyStorePath));
			trustStore.load(instream, keyStorepassword.toCharArray());
			//相信自定义的所有证书
			sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()) .build();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				instream.close();
			} catch (IOException e) {}
		}
		return this;
    }
}
