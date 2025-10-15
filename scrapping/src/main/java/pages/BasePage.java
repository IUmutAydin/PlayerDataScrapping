package pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class BasePage {

	protected static WebDriver driver;

	public void setDriver(WebDriver driver) {
		BasePage.driver = driver;
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
}
