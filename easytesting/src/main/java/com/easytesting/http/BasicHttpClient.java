package com.easytesting.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easytesting.http.HttpConstants;
import com.easytesting.http.MultiMap;
import com.easytesting.ssl.EasySSLProtocolSocketFactory;

public class BasicHttpClient {

	private static final Logger log = LoggerFactory
			.getLogger(BasicHttpClient.class);
	private final HttpClient httpClient;
	private int statusCode = -1;
	private byte[] responseBody = null;
	private Map<String, String> responseHeaders = null;
	private Boolean ignoreCookie = null;

	/**
	 * No argument constructor
	 */
	public BasicHttpClient() {
		httpClient = new HttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(0, false));
	}

	/**
	 * Constructor creates a HttpClient with a state
	 * 
	 * @param state
	 */
	public BasicHttpClient(HttpState state) {
		this();
		httpClient.setState(state);
	}

	/**
	 * Constructor
	 * 
	 * @param hostname
	 *            - hostname of service to which we will talk
	 * @param port
	 *            - port that we are going to connect to
	 */
	public BasicHttpClient(String hostname, int port) {
		httpClient = new HttpClient();
		HostConfiguration hostConf = new HostConfiguration();
		if (hostname != null && !"".equals(hostname)) {
			if (port < 1 || port > Integer.MAX_VALUE) {
				hostConf.setHost(hostname);
			} else {
				hostConf.setHost(hostname, port);
			}
		} else {
			throw new IllegalArgumentException("Host should not be null/empty");
		}
	}

	/**
	 * Setter method to set Parameters for HttpClient
	 * 
	 * @param param
	 *            - HttpClient parameters to be set
	 */
	public void setParams(HttpClientParams param) {
		httpClient.setParams(param);
	}

	/**
	 * Getter method to get Parameters of HttpClient
	 * 
	 * @return param - HttpClient parameters
	 */
	public HttpClientParams getParams() {
		return httpClient.getParams();
	}

	/**
	 * Setter method to set HostConfiguration
	 * 
	 * @param hConfig
	 *            - HostConfiguration of the HttpClient
	 */
	public void setHostConfiguration(HostConfiguration hConfig) {
		httpClient.setHostConfiguration(hConfig);
	}

	/**
	 * Getter method to get HostConfiguration
	 * 
	 * @return HostConfiguration of HttpClient
	 */
	public HostConfiguration getHostConfiguration() {
		return httpClient.getHostConfiguration();
	}

	/**
	 * Getter method to get HttpConnectionManager of a HttpClient
	 * 
	 * @return HttpConnectionManager of HttpClient
	 */
	public HttpConnectionManager getHttpConnectionManager() {
		return httpClient.getHttpConnectionManager();
	}

	/**
	 * Setter method to set HttpConnectionManager of a HttpClient
	 * 
	 * @param hcManager
	 */
	public void setHttpConnectionManager(HttpConnectionManager hcManager) {
		httpClient.setHttpConnectionManager(hcManager);
	}

	/**
	 * Executes a HTTP Get request and populates the response status code,
	 * response body and response headers using the resource URL.
	 * 
	 * @param url
	 *            - resource URL
	 * 
	 * @throws IOException
	 */
	public void doHttpGet(String url) throws IOException {
		doHttpGet(url, null, null, null);
	}

	/**
	 * Executes a HTTP Get request and populates the response status code,
	 * response body and response headers using the resource URL. Requires
	 * resource URL and Headers Map.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * 
	 * @throws IOException
	 */
	public void doHttpGet(String url, MultiMap<String, String> addRequestHeaders)
			throws IOException {
		doHttpGet(url, addRequestHeaders, null, null);
	}

	/**
	 * Executes a HTTP Get request and populates the response status code,
	 * response body and response headers. Requires URL, request headers and
	 * query string or parameters to be added.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param parameters
	 *            - Map containing parameter(Name, Value) pairs that together
	 *            form the request parameters
	 * 
	 * @throws IOException
	 */
	public void doHttpGet(String url,
			MultiMap<String, String> addRequestHeaders,
			MultiMap<String, String> parameters) throws IOException {
		doHttpGet(url, addRequestHeaders, null, parameters);
	}

	/**
	 * Executes a HTTP Get request and populates the response status code,
	 * response body and response headers. Requires request headers and query
	 * string or parameters to be added and the headers to be removed.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param removeRequestHeaders
	 *            - Request headers that should be removed from the HTTP request
	 * @param queryString
	 *            - Map containing query(Name, Value) pairs that together form
	 *            the query string
	 * 
	 * @throws IOException
	 */
	private void doHttpGet(String url,
			MultiMap<String, String> addRequestHeaders,
			List<String> removeRequestHeaders,
			MultiMap<String, String> queryString) throws IOException {
		GetMethod method = new GetMethod(url);
		manualCookieHandler(method);
		addRequestHeaders(method, addRequestHeaders);
		removeRequestHeaders(method, removeRequestHeaders);
		addQueryString(method, queryString);
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP HEAD request and populates the response status code, and
	 * response headers. Requires resource URL.
	 * 
	 * @param url
	 *            - resource URL
	 * 
	 * @throws IOException
	 */
	public void doHttpHead(String url) throws IOException {
		doHttpHead(url, null, null);

	}

	/**
	 * Executes a HTTP HEAD request and populates the response status code, and
	 * response headers. Requires URL and request headers to be added.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * 
	 * @throws IOException
	 */
	public void doHttpHead(String url,
			MultiMap<String, String> addRequestHeaders) throws IOException {
		doHttpHead(url, addRequestHeaders, null);
	}

	/**
	 * Executes a HTTP HEAD request and populates the response status code, and
	 * response headers. Requires URL, headers and query string to be added to
	 * the request.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param queryString
	 *            - Map containing query(Name, Value) pairs that together form
	 *            the query string
	 * 
	 * @throws IOException
	 */
	public void doHttpHead(String url,
			MultiMap<String, String> addRequestHeaders,
			MultiMap<String, String> queryString) throws IOException {
		HeadMethod method = new HeadMethod(url);
		addRequestHeaders(method, addRequestHeaders);
		addQueryString(method, queryString);
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP PUT request with request parameters but without the
	 * request body. Populates the response status code, response body and
	 * response headers. Requires resource URL, headers and parameters to be
	 * added to request.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param queryParameters
	 *            - Parameters to be added to the request
	 * 
	 * @throws IOException
	 */
	public void doHttpPut(String url,
			MultiMap<String, String> addRequestHeaders,
			MultiMap<String, String> queryParameters) throws IOException {
		doHttpPut(url, addRequestHeaders, queryParameters, null);
	}

	/**
	 * Executes a HTTP PUT request where content is passed as a string.
	 * Populates the response status code, response body and response headers.
	 * Requires resource URL, headers and body content to be added to request.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param fileContents
	 *            - Content of the request body as a string
	 * 
	 * @throws IOException
	 */
	public void doHttpPut(String url,
			MultiMap<String, String> addRequestHeaders, String fileContents)
			throws IOException {
		doHttpPut(url, addRequestHeaders, null, fileContents);
	}

	/**
	 * Executes a HTTP PUT request where content is passed as a string.
	 * Populates the response status code, response body and response headers.
	 * Requires resource URL, headers, parameters and body content to be added
	 * to request.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param queryParameters
	 *            - Request parameters to be added to the request
	 * @param fileContents
	 *            - Content of the request body as a string
	 * 
	 * @throws IOException
	 */
	private void doHttpPut(String url,
			MultiMap<String, String> addRequestHeaders,
			MultiMap<String, String> queryParameters, String fileContents)
			throws IOException {

		PutMethod method = new PutMethod(url);
		manualCookieHandler(method);
		addRequestHeaders(method, addRequestHeaders);
		addQueryString(method, queryParameters);
		try {
			if (fileContents != null) {
				String contentType = null;
				if (addRequestHeaders
						.containsKey(HttpConstants.HEADER_CONTENT_TYPE)) {
					contentType = addRequestHeaders.get(
							HttpConstants.HEADER_CONTENT_TYPE).get(0);
				}
				RequestEntity requestBody = new StringRequestEntity(
						fileContents, contentType, null);
				method.setRequestEntity(requestBody);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage());
		}
		this.executeMethod(method);
	}

	/**
	 * Executes a Multipart Http PUT request. The files to be PUT are to be
	 * added to the fileMap. Any String content to be PUT can be added to the
	 * stringMap
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request
	 * @param fileMap
	 *            - Map containing the key to identify each part of the
	 *            multipart request mapped to the absolute path of the file to
	 *            be PUT
	 * @param stringMap
	 *            - Map containing the key to identify each part of the
	 *            multipart request mapped to the String value to be PUT
	 * @throws IOException
	 *             , FileNotFoundException
	 */
	public void doMultiPartHttpPut(String url,
			MultiMap<String, String> addRequestHeaders,
			Map<String, String> fileMap, Map<String, String> stringMap)
			throws IOException, FileNotFoundException

	{
		PutMethod method = new PutMethod(url);
		addRequestHeaders(method, addRequestHeaders);
		int fileMapSize = 0;
		int stringMapSize = 0;
		if (fileMap != null) {
			fileMapSize = fileMap.size();
		}
		if (stringMap != null) {
			stringMapSize = stringMap.size();
		}
		Part[] parts = new Part[fileMapSize + stringMapSize];
		int index = 0;
		try {
			if (fileMap != null) {
				Part filePart = null;
				String filePath = null;
				File file = null;
				for (String key : fileMap.keySet()) {
					filePath = fileMap.get(key);
					file = new File(filePath);
					filePart = new FilePart(key, file);
					parts[index] = filePart;
					index++;
				}
			}
			if (stringMap != null) {
				Part stringPart = null;
				String value = null;
				for (String key : stringMap.keySet()) {
					value = stringMap.get(key);
					stringPart = new StringPart(key, value);
					parts[index] = stringPart;
					index++;
				}
			}
			RequestEntity requestBody = new MultipartRequestEntity(parts,
					method.getParams());
			method.setRequestEntity(requestBody);
		} catch (FileNotFoundException fne) {
			throw new FileNotFoundException(fne.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP PUT request where the file is passed as an InputStream.
	 * Populates the response status code, response body and response headers.
	 * Requires resource URL , headers to be added and file as an input stream.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param fileStream
	 *            - Content of the request body
	 * 
	 * @throws IOException
	 */
	public void doHttpPut(String url,
			MultiMap<String, String> addRequestHeaders, InputStream fileStream)
			throws IOException {
		PutMethod method = new PutMethod(url);
		String contentType = null;
		if (addRequestHeaders.containsKey(HttpConstants.HEADER_CONTENT_TYPE)) {
			contentType = addRequestHeaders.get(
					HttpConstants.HEADER_CONTENT_TYPE).get(0);
		}
		addRequestHeaders(method, addRequestHeaders);
		RequestEntity requestBody = new InputStreamRequestEntity(fileStream,
				contentType);
		method.setRequestEntity(requestBody);
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP Post request and populates the response status code,
	 * response body and response headers. Requires URL and parameters map
	 * consisting of name, value pairs.
	 * 
	 * @param url
	 *            - resource URL
	 * @param parameters
	 *            - Map of (name, value) that form the request parameters
	 * 
	 * @throws IOException
	 */
	public void doHttpPost(String url, MultiMap<String, String> parameters)
			throws IOException {
		doHttpPost(url, null, null, parameters);
	}

	/**
	 * Executes a HTTP Post request and populates the response status code,
	 * response body and response headers. Requires URL, request headers and
	 * parameters.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param parameters
	 *            - Map containing parameter(Name, Value) pairs
	 * 
	 * @throws IOException
	 */
	public void doHttpPost(String url,
			MultiMap<String, String> addRequestHeaders,
			MultiMap<String, String> parameters) throws IOException {
		this.doHttpPost(url, addRequestHeaders, null, parameters);
	}

	/**
	 * Executes a HTTP Post request and populates the response status code,
	 * response body and response headers. Takes in URL, headers to be added and
	 * removed and parameters.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param removeRequestHeaders
	 *            - Request headers that should be removed from the HTTP request
	 * @param parameters
	 *            - parameters to be added to the request body
	 * 
	 * @throws IOException
	 */
	private void doHttpPost(String url,
			MultiMap<String, String> addRequestHeaders,
			List<String> removeRequestHeaders,
			MultiMap<String, String> parameters) throws IOException {
		PostMethod method = new PostMethod(url);
		manualCookieHandler(method);
		this.addRequestHeaders(method, addRequestHeaders);
		this.removeRequestHeaders(method, removeRequestHeaders);
		this.addParameters(method, parameters);
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP Post request provided a body to send to the server and
	 * populates the response status code, response body and response headers.
	 * Takes in URL, and the body content to be posted.
	 * 
	 * @param url
	 *            - resource URL
	 * @param bodyContent
	 *            - HTTP Request body content to be sent to server
	 * 
	 *            TODO: Method signature is given url as second parameter due to
	 *            the ambiguity in other doHttpPost() methods that are called as
	 *            doHttpPost(url, null);
	 * 
	 * @throws IOException
	 */
	public void doHttpPost(StringBuffer bodyContent, String url)
			throws IOException {
		PostMethod method = new PostMethod(url);
		manualCookieHandler(method);
		method.setRequestEntity(new StringRequestEntity(bodyContent.toString()));
		method.setRequestHeader("Content-type", "text/plain; charset=utf-8");
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP delete request and populates the response status code,
	 * response body and response headers. Requires only resource URL.
	 * 
	 * @param url
	 *            - resource URL
	 * 
	 * @throws IOException
	 */
	public void doHttpDelete(String url) throws IOException {
		doHttpDelete(url, null, null, null);
	}

	/**
	 * Executes a HTTP delete request and populates the response status code,
	 * response body and response headers. Requires resource URL and headers.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * 
	 * @throws IOException
	 */
	public void doHttpDelete(String url,
			MultiMap<String, String> addRequestHeaders) throws IOException {
		doHttpDelete(url, addRequestHeaders, null, null);
	}

	/**
	 * Executes a HTTP delete request and populates the response status code,
	 * response body and response headers. Requires resource URL, headers and
	 * parameters for the request.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            - Request headers that should be added to the request.
	 * @param parameters
	 *            - The parameters for the request
	 * 
	 * @throws IOException
	 */
	public void doHttpDelete(String url,
			MultiMap<String, String> addRequestHeaders,
			MultiMap<String, String> parameters) throws IOException {
		doHttpDelete(url, addRequestHeaders, null, parameters);
	}

	/**
	 * Executes a HTTP delete request and populates the response status code,
	 * response body and response headers. Takes in URL, headers to be set and
	 * removed and request parameters.
	 * 
	 * @param url
	 *            - resource URL
	 * @param addRequestHeaders
	 *            Request headers that should be added to the request.
	 * @param removeRequestHeaders
	 *            - Request headers that should be removed from the HTTP request
	 * @param parameters
	 *            - The parameters for the request
	 * 
	 * @throws IOException
	 */
	private void doHttpDelete(String url,
			MultiMap<String, String> addRequestHeaders,
			List<String> removeRequestHeaders,
			MultiMap<String, String> parameters) throws IOException {
		DeleteMethod method = new DeleteMethod(url);
		this.addRequestHeaders(method, addRequestHeaders);
		this.removeRequestHeaders(method, removeRequestHeaders);
		this.addQueryString(method, parameters);
		this.executeMethod(method);
	}

	/**
	 * Executes a HTTP request method and populates the response status code,
	 * response body and response headers. In the end, the method's connection
	 * is released.
	 * 
	 * @param method
	 *            - Method to be executed
	 * 
	 * @throws IOException
	 */
	private void executeMethod(HttpMethod method) throws IOException {
		try {
			log.info("HTTP Client: " + method.getName() + " to URL "
					+ method.getURI());
			this.statusCode = httpClient.executeMethod(method);
			storeResponseHeaders(method.getResponseHeaders());
			storeResponseBody(method.getResponseBodyAsStream());
		} catch (HttpException e) {
			/*
			 * We want to avoid letting client know about Jakarta common HTTP
			 * client. So, we catch this exception here, display error message
			 * and throw an IO exception.
			 */
			log.error("Fatal protocol violation: " + e.getMessage());
			throw new IOException(e.getMessage());
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Gets the response body of an executed HTTP request method as a String
	 * 
	 * @return the response body as string
	 */
	public String getResponseBody() {
		return new String(this.responseBody);
	}

	/**
	 * Gets the response body of an executed HTTP request method as a String by
	 * decoding the specified array of bytes using the specified charset.
	 * 
	 * @param charsetName
	 *            the charset used to decode the byte array
	 * 
	 * @return the response body as string after decoding using the charset.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public String getResponseBody(String charsetName)
			throws UnsupportedEncodingException {
		return new String(this.responseBody, charsetName);
	}

	/**
	 * Gets the response body of an executed HTTP request method as an
	 * InputStream
	 * 
	 * @return the response body as a stream
	 */
	public InputStream getResponseBodyAsStream() {
		return this.responseBody != null ? new ByteArrayInputStream(
				this.responseBody) : null;
	}

	/**
	 * Gets the response headers of an executed HTTP request method
	 * 
	 * @return the response headers as Map<headerName,value>
	 */
	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	/**
	 * Gets the status code returned on execution of a HTTP request method
	 * 
	 * @return the response body as string
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the username and password required for Basic Authentication
	 * 
	 * @param username
	 *            - username for request
	 * @param password
	 *            - password associated with the specified username
	 */
	public void setAuthentication(String username, String password) {
		String hostname = null;
		int port = -1;
		HostConfiguration hostConfig = httpClient.getHostConfiguration();
		if (hostConfig != null) {
			hostname = hostConfig.getHost();
			port = hostConfig.getPort();
			if (hostname == null) {
				log.warn("setAuthentication:: AuthScope is set for "
						+ "any/every host");
			}
		}
		/*
		 * Specify the authentication scope and set the credentials
		 */
		httpClient.getState().setCredentials(
				new AuthScope(hostname, port, AuthScope.ANY_REALM,
						AuthScope.ANY_SCHEME),
				new UsernamePasswordCredentials(username, password));
	}

	/**
	 * gets state of the http client(session state)
	 * 
	 * @return client state
	 */
	public HttpState getState() {
		return httpClient.getState();
	}

	/**
	 * sets the http client with the specfied state ( session state) if the
	 * state is null an empty state object is created and set
	 * 
	 * @param state
	 *            - http client state
	 */
	public void setState(HttpState state) {
		if (state == null) {
			state = new HttpState();
		}
		httpClient.setState(state);
	}

	/**
	 * Stores the response body generated after execution of a HTTP method into
	 * a byte array.
	 * 
	 * @param responseBody
	 *            - response body of HTTP method
	 * 
	 * @throws IOException
	 */
	private void storeResponseBody(InputStream responseBody) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		if (responseBody != null) {
			while ((bytesRead = responseBody.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			this.responseBody = out.toByteArray();
		} else {
			this.responseBody = null;
		}
	}

	/**
	 * Stores the response headers after execution of a HTTP method
	 * 
	 * @param responseHeaders
	 *            - response headers of HTTP method
	 */
	private void storeResponseHeaders(Header[] responseHeaders) {
		if (responseHeaders != null) {
			Map<String, String> headers = new HashMap<String, String>();
			for (Header header : responseHeaders) {
				headers.put(header.getName(), header.getValue());
			}
			if (!headers.isEmpty()) {
				this.responseHeaders = headers;
			}
		} else {
			this.responseHeaders = null;
		}
	}

	/**
	 * Add Request headers to the specified HTTP method
	 * 
	 * @param method
	 *            the HTTP method
	 * @param addRequestHeaders
	 *            request headers to be added.
	 */
	private void addRequestHeaders(HttpMethod method,
			MultiMap<String, String> addRequestHeaders) {
		if (addRequestHeaders != null) {
			for (String headerName : addRequestHeaders.keySet()) {
				for (String headerVal : addRequestHeaders.get(headerName)) {
					method.addRequestHeader(headerName, headerVal);
				}
			}
		}
	}

	/**
	 * Add query strings to the specified HTTP method
	 * 
	 * @param method
	 *            the HTTP method
	 * @param queryString
	 *            query strings to be added.
	 */
	private void addQueryString(HttpMethod method,
			MultiMap<String, String> queryString) {
		if (queryString != null) {
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			for (String queryName : queryString.keySet()) {
				for (String queryVal : queryString.get(queryName)) {
					NameValuePair pair = new NameValuePair(queryName, queryVal);
					pairList.add(pair);
				}
			}
			if (!pairList.isEmpty()) {
				method.setQueryString(pairList.toArray(new NameValuePair[] {}));
			}
		}
	}

	/**
	 * Add parameters to the POST HTTP method request body.
	 * 
	 * @param postMethod
	 *            the HTTP POST method
	 * @param parameters
	 *            the parameters to be added to the request body.
	 */
	private void addParameters(PostMethod postMethod,
			MultiMap<String, String> parameters) {
		if (parameters != null) {
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			for (String queryName : parameters.keySet()) {
				for (String queryVal : parameters.get(queryName)) {
					NameValuePair pair = new NameValuePair(queryName, queryVal);
					pairList.add(pair);
				}
			}
			if (!pairList.isEmpty()) {
				postMethod.addParameters(pairList
						.toArray(new NameValuePair[] {}));
			}
		}
	}

	/**
	 * Removes the specified request headers if they exist
	 * 
	 * @param method
	 *            the HTTP method
	 * @param removeRequestHeaders
	 *            Request headers to be removed
	 */
	private void removeRequestHeaders(HttpMethod method,
			List<String> removeRequestHeaders) {
		if (removeRequestHeaders != null) {
			for (String headerName : removeRequestHeaders) {
				method.removeRequestHeader(headerName);
			}
		}
	}

	/**
	 * Method to enable manual cookie handling.
	 * 
	 * @param method
	 *            the HTTP method
	 */
	private void manualCookieHandler(HttpMethod method) {
		if (this.ignoreCookie != null) {
			if (this.ignoreCookie) {
				method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
				ignoreCookie = false;
			}
		}
	}

	/**
	 * Set the value of the ignoreCookie attribute.
	 * 
	 * @param ignoreCookie
	 *            the ignoreCookie to set
	 */
	public void setIgnoreCookie(Boolean ignoreCookie) {
		this.ignoreCookie = ignoreCookie;
	}

	/**
	 * Register EasySSLSocketFactory to ignore validating certificates on ssl
	 * connections on the client side
	 */
	public static void disableSSLValidation() {
		Protocol myhttps = new Protocol("https",
				new EasySSLProtocolSocketFactory(), 443);
		Protocol.unregisterProtocol("https");
		Protocol.registerProtocol("https", myhttps);

	}

	public static void main(String args[]) {
		final BasicHttpClient httpClient = new BasicHttpClient();
		final String url = "https://mail.126.com/entry/cgi/ntesdoor?";

		Protocol.unregisterProtocol("https");
		Protocol.registerProtocol("https", new Protocol("https",
				new EasySSLProtocolSocketFactory(), 443));
		try {
			httpClient.doHttpPost(url, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final int statusCode = httpClient.getStatusCode();
		final String responseBody = httpClient.getResponseBody();
		log.info("Status code: " + statusCode);
	}

}
