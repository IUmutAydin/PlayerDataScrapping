package utilities;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import optaAnalystPages.DriverManager;

public class WaitUtilities extends Utility {

	public static void explicitlyWaitUntilVisible(int seconds, By locator) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)));
	}

	public static void explicitlyWaitUntilVisible(int seconds, WebElement... elements) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElements(elements)));
	}

	public static void explicitlyWaitUntilPresent(int seconds, By locator) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)));
	}

	public static void explicitlyWaitUntilPresent(int seconds, By locator, int refreshNumber, Runnable afterRefresh) {
		WebDriver driver = DriverManager.getDriver();

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
			wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)));
		} catch (Exception e) {
			if (refreshNumber > 0) {
				driver.navigate().refresh();

				if (afterRefresh != null) {
					afterRefresh.run();
				}

				explicitlyWaitUntilPresent(seconds, locator, refreshNumber - 1, afterRefresh);
			} else
				explicitlyWaitUntilPresent(seconds, locator);

			System.out.println(Thread.currentThread().getName() + " " + driver.getCurrentUrl() + " Refresh Number: "
					+ refreshNumber);
			e.printStackTrace();
		}
	}

	public static void explicitlyWaitUntilClickable(int seconds, By locator) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(driver.findElement(locator))));
	}

	public static void explicitlyWaitUntilVisible(int seconds, By locator, int polllingMillis) {
		WebDriver driver = DriverManager.getDriver();

		FluentWait<WebDriver> fluentWait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(seconds))
				.pollingEvery(Duration.ofMillis(polllingMillis))
				.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

		fluentWait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)));
	}

	public static void explicitlyWaitUntilVisible(int seconds, int polllingMillis, WebElement... elements) {
		WebDriver driver = DriverManager.getDriver();

		FluentWait<WebDriver> fluentWait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(seconds))
				.pollingEvery(Duration.ofMillis(polllingMillis))
				.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

		fluentWait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElements(elements)));
	}

	public static void explicitlyWaitUntilPageComplition(int seconds) {
		WebDriver driver = DriverManager.getDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until((driver1) -> ((JavascriptExecutor) driver1).executeScript("return document.readyState")
				.equals("complete"));
	}

	public static void explicitlyWaitUntilWindows(int seconds) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.refreshed(ExpectedConditions.numberOfWindowsToBe(2)));
	}

	public static void explicitlyWaitUntilFrame(int seconds, By locator) {
		WebDriver driver = DriverManager.getDriver();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions
				.refreshed(ExpectedConditions.frameToBeAvailableAndSwitchToIt(driver.findElement(locator))));
	}

}
