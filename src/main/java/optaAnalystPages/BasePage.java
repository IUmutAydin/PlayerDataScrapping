package optaAnalystPages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import utilities.JavaScriptUtilities;
import utilities.WaitUtilities;

public class BasePage {

	protected WebDriver driver;
	protected final String url = "https://theanalyst.com";
	protected String previousURL;

	public BasePage() {
		driver = DriverManager.getDriver();
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	protected WebElement find(By locator) {
		return driver.findElement(locator);
	}

	protected List<WebElement> findElements(By locator) {
		return driver.findElements(locator);
	}

	protected void set(By locator, String text) {
		find(locator).clear();
		find(locator).sendKeys(text);
	}

	protected void click(By locator) {
		find(locator).click();
	}

	protected void click(WebElement webElement) {
		Actions actions = new Actions(driver);
		actions.moveToElement(webElement).click().build().perform();
	}

	protected void moveToElement(WebElement webElement) {
		Actions actions = new Actions(driver);
		actions.moveToElement(webElement).build().perform();
	}

	public void backPage() {
		driver.navigate().back();
		driver.navigate().to(driver.getCurrentUrl());
	}

	public void forwardPage() {
		driver.navigate().forward();
		driver.navigate().to(driver.getCurrentUrl());
	}

	public void navigateToURL(String url) {
		driver.navigate().to(url);
	}

	public void navigateToPreviousURL() {
		driver.navigate().to(previousURL);
	}

	public String getPreviousURL() {
		return previousURL;
	}

	public void setPreviousURL(String previousURL) {
		this.previousURL = previousURL;
	}

	protected void waitUntilVisibleThenScroll(int seconds, By locator) {
		WaitUtilities.explicitlyWaitUntilVisible(seconds, locator);
		JavaScriptUtilities.scrollToElement(locator);
	}

	protected void waitUntilVisibleThenScroll(int seconds, By locator, int refreshNumber) {
		try {
			waitUntilVisibleThenScroll(seconds, locator);
		} catch (NoSuchElementException e) {
			if (refreshNumber > 0) {
				driver.navigate().refresh();
				waitUntilVisibleThenScroll(seconds, locator, refreshNumber - 1);
			} else
				waitUntilVisibleThenScroll(seconds, locator);

			System.out.println(Thread.currentThread().getName() + " " + driver.getCurrentUrl() + " Refresh Number: "
					+ refreshNumber);
			e.printStackTrace();
		}
	}

	protected void waitUntilClickableThenScroll(int seconds, By locator) {
		WaitUtilities.explicitlyWaitUntilClickable(seconds, locator);
		JavaScriptUtilities.scrollToElement(locator);
	}

	protected void waitUntilClickableThenScroll(int seconds, By locator, int refreshNumber) {
		try {
			WaitUtilities.explicitlyWaitUntilClickable(seconds, locator);
			JavaScriptUtilities.scrollToElement(locator);
		} catch (NoSuchElementException e) {
			if (refreshNumber > 0) {
				driver.navigate().refresh();
				waitUntilClickableThenScroll(seconds, locator, refreshNumber - 1);
			} else
				waitUntilClickableThenScroll(seconds, locator);

			System.out.println(Thread.currentThread().getName() + " " + driver.getCurrentUrl() + " Refresh Number: "
					+ refreshNumber);
			e.printStackTrace();
		}
	}

	protected void waitUntilClickableThenScroll(int seconds, By locator, int refreshNumber, Runnable afterRefresh) {
		try {
			WaitUtilities.explicitlyWaitUntilClickable(seconds, locator);
			JavaScriptUtilities.scrollToElement(locator);
		} catch (NoSuchElementException e) {
			if (refreshNumber > 0) {
				driver.navigate().refresh();
				afterRefresh.run();
				waitUntilClickableThenScroll(seconds, locator, refreshNumber - 1);
			} else
				waitUntilClickableThenScroll(seconds, locator);

			System.out.println(Thread.currentThread().getName() + " " + driver.getCurrentUrl() + " Refresh Number: "
					+ refreshNumber);
			e.printStackTrace();
		}
	}
}
