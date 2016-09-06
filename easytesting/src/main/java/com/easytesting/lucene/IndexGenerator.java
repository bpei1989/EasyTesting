package com.easytesting.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easytesting.util.IOUtil;
import com.easytesting.util.PropertiesUtil;
import com.easytesting.util.FileUtil;
import com.easytesting.util.StringUtil;
import com.easytesting.util.IOUtil;
import com.easytesting.lucene.Doc;

public class IndexGenerator {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexGenerator.class);
	private static String FILE_PATH;
	private static List<File> fileList;

	private static StandardAnalyzer analyzer;
	private static Directory index;
	private static IndexWriterConfig config;

	private IndexGenerator() {}

	public static IndexGenerator generateIndex() {
		analyzer = new StandardAnalyzer();
		index = new RAMDirectory();
		config = new IndexWriterConfig(analyzer);
		Properties props = PropertiesUtil
				.loadProps("src/main/resources/searchFilePah.properties");
		FILE_PATH = PropertiesUtil.getString(props, "path");
		fileList = new ArrayList<File>();
		return new IndexGenerator();
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
				content = new String(IOUtil.defaultByteReader(in));
				Doc d = new Doc();
				d.setContent(content);
				d.setFilename(file.getName());
				d.setPath(file.getPath());
				initIndexWriter(index, config, list);
				content = "";
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		Date end = new Date();
		logger.info("索引创建耗时： " + (begin.getTime() - end.getTime()));
		return true;
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

}
