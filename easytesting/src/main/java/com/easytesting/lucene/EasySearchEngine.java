package com.easytesting.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easytesting.util.IOUtil;
import com.easytesting.util.PropertiesUtil;
import com.easytesting.util.FileUtil;
import com.easytesting.lucene.Doc;


/**
 * 一个简单的搜索引擎，基于Lucene
 * 在resources里的searchFilePah.properties配置索引文件路径，在searchKeyWorkd.properties里配置搜索关键字
 * 可以用于搜索自动化测试的日志，查看错误的出现频率
 */
public class EasySearchEngine {

	private static final Logger logger = LoggerFactory.getLogger(EasySearchEngine.class);
	private static String FILE_PATH;
	private static List<File> fileList;
	private static int HIT = 10;

	private static StandardAnalyzer analyzer;
	private static Directory index;
	private static IndexWriterConfig config;

	private EasySearchEngine() {}

	public static EasySearchEngine newInstance() {
		analyzer = new StandardAnalyzer();
		index = new RAMDirectory();
		config = new IndexWriterConfig(analyzer);
		Properties props = PropertiesUtil
				.loadProps("searchFilePah.properties");
		FILE_PATH = PropertiesUtil.getString(props, "path");
		fileList = new ArrayList<File>();
		return new EasySearchEngine();
	}

	public static boolean createIndex() {
		Date begin = new Date();
		String content;
		InputStream in;
		try {
			FileUtil.getFileRecursively(new File(FILE_PATH), fileList);
			List<Doc> list = new ArrayList<Doc>();
			if (fileList.isEmpty()) {
				logger.error("文件列表为空");
				return false;
			}

			for (File file : fileList) {
				in = new FileInputStream(file);
				//content = IOUtil.readContentToString("UTF-8", in);
				content = getErrorLine(in);
				Doc d = new Doc();
				d.setContent(content);
				d.setFilename(file.getName());
				d.setPath(file.getPath());
				list.add(d);
				content = "";
			}
			initIndexWriter(index, config, list);

		} catch (Exception e) {
			logger.error("", e);
		}
		Date end = new Date();
		long time = begin.getTime() - end.getTime();
		logger.info("索引创建耗时： " + time + "毫秒");
		return true;
	}

	public static boolean createIndex(String endWith) {
		Date begin = new Date();
		String content;
		InputStream in;
		try {
			FileUtil.getFileRecursively(new File(FILE_PATH), fileList);
			List<Doc> list = new ArrayList<Doc>();
			if (fileList.isEmpty()) {
				logger.error("文件列表为空");
				return false;
			}

			for (File file : fileList) {
				if (!file.getName().endsWith(endWith)) {
					continue;
				}
				in = new FileInputStream(file);
				content = getErrorLine(in);
				//System.out.println("content:" + content);
				Doc d = new Doc();
				d.setContent(content);
				d.setFilename(file.getName());
				d.setPath(file.getPath());
				list.add(d);
				content = "";
			}
			initIndexWriter(index, config, list);

		} catch (Exception e) {
			logger.error("", e);
		}
		Date end = new Date();
		long time = begin.getTime() - end.getTime();
		logger.info("索引创建耗时： " + time + "毫秒");
		return true;
	}
	
	public static String getErrorLine(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder result = new StringBuilder();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(line.contains("[ERROR]") || line.contains("[FAIL]")) {
					result.append(line);
					result.append("\n");
				}
			}
			return result.toString();
		} finally {
			reader.close();
		}
	}
	
	private static void initIndexWriter(Directory index,
			IndexWriterConfig config, List<Doc> docs) throws IOException {
		IndexWriter w;
		try {
			w = new IndexWriter(index, config);
			for (Doc d : docs) {
				addDoc(w, d);
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}


	private static void addDoc(IndexWriter w, Doc doc) throws IOException {
		Document document = new Document();
		document.add(new TextField("filename", doc.getFilename(), Store.YES));
		document.add(new TextField("content", doc.getContent(), Store.YES));
		document.add(new TextField("path", doc.getPath(), Store.YES));
		w.addDocument(document);
	}
	
	public static void searchResultWithPropers() {
		try {
			
			Properties props = PropertiesUtil.loadProps("searchKeyWorkd.properties");
			String queryText = PropertiesUtil.getString(props, "keyword");
			logger.info("query: " + queryText);
			Query query = new QueryParser("content", analyzer).parse(queryText);
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(query, HIT);
			ScoreDoc[] hits = docs.scoreDocs;
			logger.info("得分最高的前 " + hits.length + " 文件.");
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = searcher.doc(hits[i].doc);
				logger.info("*************************************************");
				logger.info(hitDoc.get("filename"));
				logger.info(hitDoc.get("content"));
				logger.info(hitDoc.get("path"));
				logger.info("*************************************************");
				logger.info("");
			}
			reader.close();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public static List<String> searchResult(String queryText) {
		List<String> errorLines = new ArrayList<String>();
		try {
			logger.info("query: " + queryText);
			Query query = new QueryParser("content", analyzer).parse(queryText);
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(query, HIT);
			ScoreDoc[] hits = docs.scoreDocs;
			logger.info("得分最高的前 " + hits.length + " 文件.");
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = searcher.doc(hits[i].doc);
				logger.info("*************************************************");
				logger.info(hitDoc.get("filename"));
				logger.info(hitDoc.get("content"));
				errorLines.add(hitDoc.get("content"));
				logger.info(hitDoc.get("path"));
				logger.info("*************************************************");
				logger.info("");
			}
			reader.close();
		} catch (Exception e) {
			logger.error("", e);
		}
		return errorLines;
	}
	

}
