package com.easytesting.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebDriver工厂类，封装了driver路径和常用的初始化driver的方法
 */
public class EasyWebDriverFactory {
	
	private static final Logger log = LoggerFactory
			.getLogger(EasyWebDriverFactory.class);


	public static WebDriver getDefaultChromeDriver() {
		System.setProperty("webdriver.chrome.driver","selenium-driver//chromedriver.exe");
		log.info("Getting chrome driver...");
		WebDriver driver= new ChromeDriver();
		driver.manage().window().maximize();
		return driver;
	}

	
	public static WebDriver getDefaultFirefoxDriver() {
		log.info("Getting firefox driver...");
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().maximize();
		return driver;
	}
	

	public static WebDriver getDefaultIEDriver() {
		DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
		caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		System.setProperty("webdriver.ie.driver","selenium-driver//IEDriverServer.exe");	
		
		log.info("Getting IE driver...");
		WebDriver driver = new InternetExplorerDriver(caps);
		driver.manage().window().maximize();
		return driver;
	}
	
	public static WebDriver getChromeDriverWithURL(String url) {
		System.setProperty("webdriver.chrome.driver","selenium-driver//chromedriver.exe");
		log.info("Getting chrome driver...");
		WebDriver driver= new ChromeDriver();
		driver.get(url);
		driver.manage().window().maximize();
		return driver;
	}

	
	public static WebDriver getDefaultFirefoxDriverWithURL(String url) {
		log.info("Getting firefox driver...");
		WebDriver driver = new FirefoxDriver();
		driver.get(url);
		driver.manage().window().maximize();
		return driver;
	}
	

	public static WebDriver getDefaultIEDriverWithURL(String url) {
		DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
		caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		System.setProperty("webdriver.ie.driver","selenium-driver//IEDriverServer.exe");	
		
		log.info("Getting IE driver...");
		WebDriver driver = new InternetExplorerDriver(caps);
		driver.get(url);
		driver.manage().window().maximize();
		return driver;
	}
	
	public static WebDriver getSpecificFirefoxDriver(String installPath) {
		System.setProperty("webdriver.firefox.bin", installPath); 
		log.info("Getting firefox driver...");
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().maximize();
		return driver;
	}
	
	public static WebDriver getSpecificFirefoxDriverWithURL(String url, String installPath) {
		System.setProperty("webdriver.firefox.bin", installPath); 
		log.info("Getting firefox driver...");
		WebDriver driver = new FirefoxDriver();
		driver.get(url);
		driver.manage().window().maximize();
		return driver;
	}
	
}
