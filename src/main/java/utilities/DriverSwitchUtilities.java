package utilities;

import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import optaAnalystPages.DriverManager;

public class DriverSwitchUtilities extends Utility {

	public static String switchNextWindow() {
		WebDriver driver = DriverManager.getDriver();

		String currentWindow = driver.getWindowHandle();

		Set<String> windowHandles = driver.getWindowHandles();

		boolean isNextWindow = false;

		for (String window : windowHandles) {
			if (window.equals(currentWindow))
				isNextWindow = true;
			else if (isNextWindow)
				driver.switchTo().window(window);
		}

		WaitUtilities.explicitlyWaitUntilWindows(10);
		WaitUtilities.explicitlyWaitUntilPageComplition(10);

		return currentWindow;
	}

	public static void switchPreviousWindow(String window) {
		WebDriver driver = DriverManager.getDriver();

		driver.close();
		driver.switchTo().window(window);
		WaitUtilities.explicitlyWaitUntilPageComplition(10);
	}

	public static void switchFrame(String frame) {
		WebDriver driver = DriverManager.getDriver();

		driver.switchTo().frame(frame);
	}

	public static void switchFrame(WebElement element) {
		WebDriver driver = DriverManager.getDriver();

		driver.switchTo().frame(element);
	}

	public static void switchDefaultContent() {
		WebDriver driver = DriverManager.getDriver();

		driver.switchTo().defaultContent();
	}
}
