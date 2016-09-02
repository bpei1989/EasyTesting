package com.easytesting.http;

public enum HttpMethods {
	/**
	 * 通过请求URI得到资源
	 */
	GET("GET"), 
	
	/**
	 * 用于添加新的内容
	 */
	POST("POST"),
	
	/**
	 * 类似于GET, 但是不返回body信息，用于检查对象是否存在，以及得到对象的元数据
	 */
	HEAD("HEAD"),
	
	/**
	 * 用于修改某个内容
	 */
	PUT("PUT"), 
	
	/**
	 * 删除某个内容
	 */
	DELETE("DELETE"), 
	
	/**
	 * 用于远程诊断服务器
	 */
	TRACE("TRACE"), 
	
	/**
	 * 询问可以执行哪些方法
	 */
	OPTIONS("TRACE"),
	
	/**
	 * 用于代理进行传输，如使用SSL
	 */
	CONNECT("CONNECT");
	
	private String name;
	
	private HttpMethods(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
