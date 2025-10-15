package utilities;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import optaAnalystPages.DriverManager;

public class JavaScriptUtilities extends Utility {

	public static void scrollToElement(By locator) {
		WebDriver driver = DriverManager.getDriver();

		scrollToElement(driver.findElement(locator));
	}

	public static void scrollToElement(WebElement element) {
		WebDriver driver = DriverManager.getDriver();

		String jsScript = "arguments[0].scrollIntoView(true);";

		((JavascriptExecutor) driver).executeScript(jsScript, element);
	}

	public static void mouseOverElement(By locator) {
		WebDriver driver = DriverManager.getDriver();

		mouseOverElement(driver.findElement(locator));
	}

	public static void mouseOverElement(WebElement element) {
		WebDriver driver = DriverManager.getDriver();

		String jsScript = "var event = new MouseEvent('mouseover', {bubbles: true, cancelable: true, view: window}); arguments[0].dispatchEvent(event);";

		((JavascriptExecutor) driver).executeScript(jsScript, element);
	}

	public static void setVisible(By locator) {
		WebDriver driver = DriverManager.getDriver();

		setVisible(driver.findElement(locator));

	}

	public static void setVisible(WebElement element) {
		WebDriver driver = DriverManager.getDriver();

		String jsScript = "arguments[0].style.display='block';";

		((JavascriptExecutor) driver).executeScript(jsScript, element);
	}

	public static void clickElement(By locator) {
		WebDriver driver = DriverManager.getDriver();

		clickElement(driver.findElement(locator));
	}

	public static void clickElement(WebElement element) {
		WebDriver driver = DriverManager.getDriver();

		String jsScript = "arguments[0].click();";

		((JavascriptExecutor) driver).executeScript(jsScript, element);
	}

	public static String getText(WebElement element) {
		WebDriver driver = DriverManager.getDriver();

		String jsScript = "return arguments[0].textContent;";

		return (String) ((JavascriptExecutor) driver).executeScript(jsScript, element);
	}

	public static void waitForElement(String cssSelector, int seconds) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(driver1 -> ((JavascriptExecutor) driver1)
				.executeScript("return !!document.querySelector(arguments[0])", cssSelector));
	}
}
