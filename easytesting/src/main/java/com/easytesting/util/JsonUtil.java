package com.easytesting.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class JsonUtil {
	private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);
	
	public static Map<?, ?> jsonToMap(String jsonStr) {
		try{
			Map<?, ?> result = (Map<?, ?>) JSON.parse(jsonStr);
			return result;
		} catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
}
