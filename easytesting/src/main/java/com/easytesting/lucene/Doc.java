package com.easytesting.lucene;

public class Doc {
	private String content;
	private String filename;
	private String path;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = new String(content);
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
