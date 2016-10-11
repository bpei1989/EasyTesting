package com.easytesting.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	
	/**
	 * 计算字符串的MD5值
	 * @param value 字符串内容
	 * @return MD5值
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String hash(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		byte[] b = value.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest(b);
		StringBuilder sb = new StringBuilder();
		for (byte tmp : hash) 
		{
			sb.append(String.format("%02X", tmp));
		}
		return sb.toString();
	}

}
