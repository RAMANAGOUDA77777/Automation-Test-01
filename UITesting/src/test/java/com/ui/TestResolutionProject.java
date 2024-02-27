package com.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.google.common.io.Files;

public class TestResolutionProject {

	public static void main(String[] args) throws Exception {
		
		MyScreenRecorderUtil.startRecording("main");

		String path = "https://www.getcalley.com/page-sitemap.xml";
		List<String> browsers = List.of("chrome", "firefox","safari");
		List<String> resolutions = List.of("1920x1080", "1366x768", "1536x864", "360x640", "414x896", "375x667");
		List<String> firstFiveUrls = getFirstFiveUrls(path);

		for (String url : firstFiveUrls) {
			// Loop through each browser
			for (String browser : browsers) {
				// Loop through each resolution
				for (String resolution : resolutions) {
					testWebsite(url, browser, resolution);
				}
			}
		}
		
		MyScreenRecorderUtil.stopRecording();

	}

	public static List<String> getFirstFiveUrls(String path) {
		WebDriver chromedriver = new ChromeDriver();
		chromedriver.get(path);
		List<WebElement> urls = chromedriver.findElements(By.xpath("//tr/td[1]"));
		List<String> firstFiveUrls = urls.stream().map(WebElement -> WebElement.getText()).collect(Collectors.toList());
		List<String> urlNeeded = firstFiveUrls.stream().limit(5).collect(Collectors.toList());
		return urlNeeded;
	}

	public static void testWebsite(String url, String browser, String resolution)
			throws IOException, InterruptedException {
		// Create a WebDriver instance for the specified browser
		WebDriver driver = createDriver(browser);

		// Split the resolution string into width and height
		String[] dimensions = resolution.split("x");
		int width = Integer.parseInt(dimensions[0]);
		int height = Integer.parseInt(dimensions[1]);

		// Set the browser window size to the desired resolution
		driver.manage().window().setSize(new Dimension(width, height));
		driver.get(url);
		//Thread.sleep(1000);

		takeScreenshot(driver, browser, resolution);

		driver.quit();
	}

	public static void takeScreenshot(WebDriver driver, String browser, String resolution) throws IOException {

		// Create a timestamp for the screenshot filename
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String fileName = String.format("%s/%s/Screenshot-%s.png", browser, resolution,
				sdf.format(System.currentTimeMillis()));

		// Create a File object for the screenshot
		File screenshotFile = new File(fileName);

		// Create the directory structure if it doesn't exist
		File parentDir = screenshotFile.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}

		// Cast the driver to TakesScreenshot interface
		TakesScreenshot screenshot = (TakesScreenshot) driver;

		// Capture the screenshot as a temporary file
		File tempScreenshot = screenshot.getScreenshotAs(OutputType.FILE);

		// Copy the temporary file to the desired location with the formatted filename
		Files.copy(tempScreenshot, screenshotFile);

	}

	public static WebDriver createDriver(String browser) {
		WebDriver driver = null;
		if (browser == "chrome") {
			driver = new ChromeDriver();
		} else if (browser == "firefox") {
			driver = new FirefoxDriver();
		} else if (browser == "safari") {
			driver = new SafariDriver();
		}
		return driver;
	}

}
