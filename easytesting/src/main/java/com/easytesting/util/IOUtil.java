package com.easytesting.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO工具类，整合Java IO的常用方法
 */

public class IOUtil {

	private static final Logger log = LoggerFactory.getLogger(IOUtil.class);
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 获取所有行并关闭流，用utf-8编码
	 * 
	 * @param inputStream
	 *            输入流
	 * @return 每行内容组成的List
	 * @throws IOException
	 */
	public static List<String> defaultStreamReader(InputStream inputStream)
			throws IOException {
		return readLinesThenClose(DEFAULT_CHARSET, inputStream);
	}

	/**
	 * 获取所有行并关闭流
	 * 
	 * @param charset
	 *            编码
	 * @param inputStream
	 *            输入流
	 * @return 每行内容组成的List
	 * @throws IOException
	 */
	public static List<String> readLinesThenClose(String charset,
			InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}

		BufferedReader reader = getBufferedReader(inputStream, charset);
		List<String> result = new ArrayList<String>();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.add(line.trim());
			}
			return result;
		} finally {
			close(reader);
		}
	}

	/**
	 * 获取BufferedReader
	 * 
	 * @param inputStream
	 *            输入流
	 * @param charsetName
	 *            编码格式
	 * @return BufferedReader
	 * @throws UnsupportedEncodingException
	 */
	private static BufferedReader getBufferedReader(InputStream inputStream,
			String charsetName) throws UnsupportedEncodingException {
		return new BufferedReader(new InputStreamReader(inputStream,
				charsetName));
	}

	/**
	 * 从输入流读取内容, 写入输出流
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @throws IOException
	 *             输入输出异常
	 */
	public static void defaultIOStreamPipe(InputStream in, OutputStream out)
			throws IOException {

		try {
			inputStreamToOutputStream(in, out, DEFAULT_BUFFER_SIZE);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * 从输入流读取内容, 写入输出流. 使用指定大小的缓冲区.
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param bufferSize
	 *            缓冲区大小(字节数)
	 * @throws IOException
	 */
	public static void inputStreamToOutputStream(InputStream in,
			OutputStream out, int bufferSize) throws IOException {
		if (bufferSize < 0) {
			log.error("bufferSize： " + bufferSize + "不合法");
		}

		byte[] buffer = new byte[bufferSize];
		int num;

		while ((num = in.read(buffer)) >= 0) {
			out.write(buffer, 0, num);
		}

		out.flush();
	}

	/**
	 * 从输入流读取内容, 写入到目标文件
	 * 
	 * @param in
	 *            输入流
	 * @param file
	 *            写入文件
	 * @throws IOException
	 */
	public static void defaultStreamWriter(InputStream in, File file)
			throws IOException {
		OutputStream out = new FileOutputStream(file);
		inputStreamToOutputStream(in, out, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 从输入流读取内容, 写入到目标文件
	 * 
	 * @param in
	 *            输入文本流
	 * @param file
	 *            写入文件
	 * @throws IOException
	 */
	public static void defaultTextWriter(Reader in, String file)
			throws IOException {
		Writer out = new FileWriter(file);
		defaultReaderWriterPipe(in, out);
	}

	/**
	 * 将指定Reader的所有文本全部读出到一个字符串中.
	 * 
	 * @param reader
	 *            要读取的<code>Reader</code>
	 * @param bufferSize
	 *            缓冲区的大小(字符数)
	 * 
	 * @return 从<code>Reader</code>中取得的文本
	 * 
	 * @throws IOException
	 *             输入输出异常
	 */
	public static String readText(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();

		readerToWriter(reader, writer, DEFAULT_BUFFER_SIZE);

		return writer.toString();
	}

	/**
	 * 将InputStream的所有内容读出到一个byte数组中。
	 * 
	 * @param in
	 *            要读取的InputStream
	 * @return byte 数组
	 * @throws IOException
	 */
	public static byte[] defaultByteReader(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		inputStreamToOutputStream(in, out, DEFAULT_BUFFER_SIZE);

		return out.toByteArray();
	}

	/**
	 * 从输入流读取内容, 写入到目标文件
	 * 
	 * @param in
	 *            输入流
	 * @param file
	 *            写入文件
	 * @throws IOException
	 */
	public static void defaultReaderWriterPipe(Reader in, Writer out)
			throws IOException {
		try {
			readerToWriter(in, out, DEFAULT_BUFFER_SIZE);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * 从输入流读取内容, 写入到输出流中，用指定大小的buffer
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param bufferSize
	 *            单位byte
	 * @throws IOException
	 */
	public static void readerToWriter(Reader in, Writer out, int bufferSize)
			throws IOException {
		if (bufferSize < 0) {
			log.error("bufferSize： " + bufferSize + "不合法");
		}

		char[] buffer = new char[bufferSize];
		int num;

		while ((num = in.read(buffer)) >= 0) {
			out.write(buffer, 0, num);
		}

		out.flush();
	}

	/**
	 * 写入数据并关闭流
	 * 
	 * @param filePath
	 *            文件路径
	 * @param datas
	 *            写入数据集合
	 * @param append
	 *            是否追加
	 * @return 是否成功
	 * @throws IOException
	 */
	public static boolean defaultStreamWriter(String filePath,
			Collection<?> datas, boolean append) throws IOException {
		return writeLinesThenClose(filePath, datas, append, DEFAULT_CHARSET);

	}

	/**
	 * 写入数据并关闭流，如果文件不存在则递归创建
	 * 
	 * @param filePath
	 *            文件路径
	 * @param datas
	 *            写入数据集合
	 * @param append
	 *            是否追加
	 * @param charsetName
	 *            编码
	 * @return 是否成功
	 * @throws IOException
	 */
	public static boolean writeLinesThenClose(String filePath,
			Collection<?> datas, boolean append, String charsetName)
			throws IOException {
		if (filePath == null || datas == null || datas.isEmpty()) {
			return false;
		}
		if (!FileUtil.exist(filePath)) {
			FileUtil.createFileRecursively(filePath);
		}

		Writer writer = null;
		try {
			writer = getWriter(filePath, append, charsetName);
			for (Object data : datas) {
				writer.append(data.toString()).append("\n");
			}
			return true;
		} finally {
			close(writer);
		}

	}

	/**
	 * 获得BufferedWriter
	 * 
	 * @param filePath
	 *            文件路径
	 * @param charsetName
	 *            编码
	 * @return BufferedWriter
	 * @throws IOException
	 * */
	private static Writer getWriter(String filePath, boolean append,
			String charsetName) throws IOException {
		OutputStream output = new FileOutputStream(filePath, append);

		Writer writer = new OutputStreamWriter(output, charsetName);
		return new BufferedWriter(writer);
	}

	/** 将字符串写入到指定<code>Writer</code>中。 */
	public static void writeText(String str, Writer out) throws IOException {
		try {
			out.write(str);
			out.flush();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	}

	/**
	 * 将byte数组写入到指定filePath中
	 * 
	 * */
	public static void defaultByteWriter(byte[] bytes, String filePath)
			throws IOException {
		writeBytes(bytes, new FileOutputStream(filePath));
	}

	/** 将byte数组写入到指定<code>OutputStream</code>中。 */
	public static void writeBytes(byte[] bytes, OutputStream out)
			throws IOException {
		try {
			out.write(bytes);
			out.flush();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 关闭流
	 * 
	 * @param Closeable
	 *            可关闭的流
	 * @throws IOException
	 */
	public static void close(Closeable closed) {
		if (closed != null) {
			try {
				closed.close();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	}
}
