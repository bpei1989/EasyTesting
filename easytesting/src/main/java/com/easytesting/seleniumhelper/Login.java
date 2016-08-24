package com.easytesting.seleniumhelper;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class Login {
	
	public static void main(String args[]) throws InterruptedException {
		WebDriver driver = WebDriverFactory.getChromeDriverWithURL("http://www.zhihu.com/");
		
		//driver.findElement(By.xpath(".//*[@id='fb-login']")).click();
		driver.findElement(By.linkText("登录")).click();
		
		//WebElement e = driver.findElement(By.name("account"));
		WebElement e = driver.findElement(By.xpath("//input[@name='account']"));
		e.sendKeys("1bingshui@sina.com");
		WebElement e1 = driver.findElement(By.xpath("//input[@name='password']"));
		e1.sendKeys("19891028ab");
		Thread.sleep(10000);
		List<WebElement> el = driver.findElements(By.className("sign-button"));
		el.get(0).click();
		//driver.findElement(By.xpath("//*[text()=’登录’]"));
		Thread.sleep(10000);
		driver.close();
	}
	
}
