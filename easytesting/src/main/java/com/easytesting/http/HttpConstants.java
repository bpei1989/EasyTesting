package com.easytesting.http;

import org.apache.commons.httpclient.HttpStatus;

/**
 * HttpConstants class contains all the constants used by other classes in this
 * package as well as the end users.
 */
public class HttpConstants
{
   public static final String CHARSET_ISO8859            = "ISO-8859-1";
   public static final String CHARSET_US_ASCII           = "US-ASCII";

   public static final String CONTENT_TEXT_PLAIN         = "text/plain";
   public static final String CONTENT_TEXT_ENRICHED      = "text/enriched";
   public static final String CONTENT_TEXT_HTML          = "text/html";
   public static final String CONTENT_TEXT_XML           = "text/xml";
   public static final String CONTENT_IMAGE_JPEG         = "image/jpeg";
   public static final String CONTENT_APP_OCTET          = "application/octet-stream";
   public static final String CONTENT_APP_WWW_FORM       = "application/x-www-form-urlencoded";
   public static final String CONTENT_APP_MSWORD         = "application/msword";
   public static final String CONTENT_MULTIPART_MIXED    = "multipart/mixed";

   public static final String CONTENT_TEXT_CHARSET_UTF8  = "text/plain;charset=UTF8";
   public static final String CONTENT_TEXT_CHARSET_ISO   = "text/plain;charset=ISO-8859-1";
   public static final String CONTENT_TEXT_CHARSET_ASCII = "text/plain;charset=US-ASCII";

   public static final String CONTENT_ENCODING_ZIP       = "gzip";

   public static final String HEADER_CONTENT_TYPE        = "Content-Type";
   public static final String HEADER_CONTENT_LENGTH      = "Content-Length";
   public static final String HEADER_LAST_MODIFIED       = "Last-Modified";
   public static final String HEADER_DATE                = "Date";
   public static final String HEADER_ETAG                = "ETag";
   public static final String HEADER_RANGE               = "Range";
   public static final String HEADER_COOKIE              = "Cookie";
   public static final String HEADER_SETCOOKIE           = "Set-Cookie";
   public static final String HEADER_CONTENT_MD5         = "Content-MD5";
   public static final String HEADER_CONTENT_ENCODING    = "Content-Encoding";
   
   public static final String HEADER_IF_MODIFIED_SINCE   = "If-Modified-Since";
   public static final String HEADER_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
   public static final String HEADER_IF_MATCH            = "If-Match";
   public static final String HEADER_IF_NONE_MATCH       = "If-None-Match";
   public static final String HEADER_IF_RANGE            = "If-Range";
   
   public static final String LINE_END_CHAR              = "\r\n";
   public static final String AUTH_HEADER                = "Authorization";
   public static final String BASIC_AUTH                 = " Basic ";
   public static final String HTTP_SSL                   = "SSL";



   public static final String URL_WITH_TMP               = "/tmp";
   public static final String INVALID_HTTP_URL           = "http;//invalidHost";
   public static final String WINDOWS_TEMP_DIR           = "c:\\temp\\vmware";
   public static final String LINUX_TEMP_DIR             = "/tmp/vmware";
   public static final String PROP_OS_NAME               = "os.name";

   /*
    * Represents the key for link name.
    */
   public static final String LINK_NAME                  = "LINK_NAME";
   /*
    * Represents the key for link.
    */
   public static final String LINK                       = "LINK";
   /*
    * Represents key for last modified date.
    */
   public static final String MODIFIED                   = "LAST_MODIFIED";
   /*
    * Represents key for capacity.
    */
   public static final String CAPACITY                   = "CAPACITY";
   /*
    * Represents the key for size.
    */
   public static final String SIZE                       = "SIZE";

   public static final int    SC_OK                      = HttpStatus.SC_OK;
   public static final int    SC_BAD_REQUEST             = HttpStatus.SC_BAD_REQUEST;
   public static final int    SC_NO_CONTENT              = HttpStatus.SC_NO_CONTENT;
   public static final int    SC_UNSUP_MEDIA             = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE;
   public static final int    SC_PARTIAL_CONTENT         = HttpStatus.SC_PARTIAL_CONTENT;
   public static final int    SC_CREATED                 = HttpStatus.SC_CREATED;
   public static final int    SC_UNAUTHORIZED            = HttpStatus.SC_UNAUTHORIZED;
   public static final int    SC_FORBIDDEN               = HttpStatus.SC_FORBIDDEN;
   public static final int    SC_NOTFOUND                = HttpStatus.SC_NOT_FOUND;
   public static final int    SC_INTERNALERROR           = HttpStatus.SC_INTERNAL_SERVER_ERROR;
   public static final int    SC_NOT_MODIFIED            = HttpStatus.SC_NOT_MODIFIED; 
   public static final int    SC_SERVICE_UNAVAILABLE     = HttpStatus.SC_SERVICE_UNAVAILABLE;
   public static final int    SC_BAD_GATEWAY             = HttpStatus.SC_BAD_GATEWAY;
   public static final int    SC_NOT_IMPLEMENTED         = HttpStatus.SC_NOT_IMPLEMENTED;
   public static final int    SC_GATEWAY_TIMEOUT         = HttpStatus.SC_GATEWAY_TIMEOUT;
   public static final int    SC_HTTP_VERSION_NOT_SUPPORTED = 
      HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED;
   public static final int    SC_ACCEPTED                = HttpStatus.SC_ACCEPTED;
   public static final int    SC_NON_AUTHORITATIVE_INFORMATION = 
      HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION;
   public static final int    SC_RESET_CONTENT           = HttpStatus.SC_RESET_CONTENT;
   public static final int    SC_PRECONDITION_FAILED     = HttpStatus.SC_PRECONDITION_FAILED;




   /** HTTP read timeout value in milli seconds. */
   public static final int    HTTP_READ_TIMEOUT_VALUE    = 60000;
   
   public static final int    HTTPS_PORT                 = 8443;
   public static final String HTTP_RESP_TITLE_TOMCAT     = "<title>Apache Tomcat</title>";
}

