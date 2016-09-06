package com.easytesting.lucene;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easytesting.util.PropertiesUtil;


public class SimpleSearchEngine {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleSearchEngine.class);
	private static StandardAnalyzer analyzer;
	private static Directory index;
	private static int HIT = 10;
	private static String queryText;

	private SimpleSearchEngine() {}
	
	public static SimpleSearchEngine newSearchEngine() {
		analyzer = new StandardAnalyzer();
		index = new RAMDirectory();
		return new SimpleSearchEngine();
	}
	
	private static void searchResult() {
		try {
		Query query = new QueryParser("content",analyzer).parse(queryText);
		StringBuilder sb = new StringBuilder();
		IndexReader reader = DirectoryReader.open(index);
		  IndexSearcher searcher = new IndexSearcher(reader);
		  TopDocs docs = searcher.search(query, HIT);
		  ScoreDoc[] hits = docs.scoreDocs;
		  logger.info("找到 " + hits.length + " 文件.");
          for (int i = 0; i < hits.length; i++) {
              Document hitDoc = searcher.doc(hits[i].doc);
    		  logger.info("*************************************************");
    		  logger.info("");
    		  logger.info("*************************************************");
          }
		  reader.close();
		  } catch (Exception e) {
			  logger.error("", e);
		  } 
	}

	public static int getHIT() {
		return HIT;
	}

	public static void setHIT(int hIT) {
		HIT = hIT;
	}
	
	public static String getqueryText() {
		return queryText;
	}

	public static void setqueryText(String text) {
		SimpleSearchEngine.queryText = queryText;
	}
	

}
